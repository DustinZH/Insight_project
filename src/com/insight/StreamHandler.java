package com.insight;

import com.insight.common.ArgumentParser;
import com.insight.model.LogEntry;
import com.insight.model.User;
import com.insight.service.Reader;
import com.insight.service.Writer;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.*;

/**
 * Created by dustin on 2018/4/3.
 */
public class StreamHandler {

    private static class UpdateEntry{
        private String lastCheckTime;
        private int count;
        private long expirationTime;

        private UpdateEntry(String lastCheckTime,int count,long expirationTime){
            this.lastCheckTime = lastCheckTime;
            this.count = count;
            this.expirationTime = expirationTime;
        }
    }
    private static Long currTime = null;
    private static Long prevTime = null;
    private static Integer period = null;
    private static Map<String,User> map = null;
    private static Map<String,UpdateEntry> update = null;
    private static PriorityQueue<User> queue;


    public static void main(String[] args) {
        Map<String,String> argMap = ArgumentParser.parseArgument(args);
        String invalidPeriodPath = argMap.get("invalidPeriod");
        getInvalidPeriod(invalidPeriodPath);

        String inputPath = argMap.get("input");
        String outputPath = argMap.get("output");

        Reader reader = new Reader(inputPath);
        Writer writer = new Writer(outputPath);
        logStream(reader, writer);
    }

    private static void getInvalidPeriod(String filePath){
        try {
            Scanner scanner = new Scanner(new FileInputStream(filePath));
            if(!scanner.hasNext()){
                throw  new java.lang.IllegalArgumentException("the invalidPeriod file should have only on number");
            }
            period = scanner.nextInt();
            scanner.close();
        }catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }




    public static void logStream(Reader reader,Writer writer){
        init(reader);

        while (!reader.isEmpty()){
            Optional<LogEntry> optional = reader.parseNextLine();
            //ignore invalid log entry
            if(optional.isPresent()){
               LogEntry entry = optional.get();

               //update current time
                currTime = getLongTime(entry.getTime());

                if(prevTime != null && prevTime <currTime){
                    removeExpiration(prevTime, writer);
                }

                //Because we remove Expired user before we update new user, now all the users are valid in the map
                if(map.containsKey(entry.getIp()) ){
                    User user = map.get(entry.getIp());
                    //check whether it has updateEntry and whether the document is duplicate
                    if(!update.containsKey(user.getIpv4()) && !user.getDocuments().contains(entry.getDocument())) { // check whether it is duplicate document
                        update.put(user.getIpv4(), new UpdateEntry(entry.getTime(), 1, currTime + period));
                        user.getDocuments().add(entry.getDocument());
                    }
                    else if(!user.getDocuments().contains(entry.getDocument())){
                        UpdateEntry updateEntry= update.get(user.getIpv4());
                        updateEntry.expirationTime = currTime + period;
                        updateEntry.lastCheckTime = entry.getTime();
                        updateEntry.count++;
                    }
                }
                else {
                    // it is a new user. must remove old user before we add new user.
                    User newUser = new User(entry.getIp(),entry.getTime(),getLongTime(entry.getTime()),currTime+period,new HashSet<>(),1);
                    newUser.getDocuments().add(entry.getDocument());
                    queue.offer(newUser);
                    map.put(newUser.getIpv4(),newUser);
                }



                prevTime = currTime;
            }
        }
        // the stream job has finished, we need put all the data into output file.
        removeAll(writer);
        reader.close();
        writer.close();
    }

    /**
     * initialize every variables when you try to do stream job
     * @param reader a log file parser,which get data line by line. simulate stream data.
     */
    private static void init(Reader reader){
        map = new HashMap<>();
        update = new HashMap<>();
//        Comparator<User> comp = Comparator.comparing(User::getExpirationTime);
//        comp.thenComparing(User::getFirstLongTypeTime);
        Comparator<User> comp = new Comparator<User>() {
            @Override
            public int compare(User o1, User o2) {
                if(o1.getExpirationTime() != o2.getExpirationTime())return(int) (o1.getExpirationTime() - o2.getExpirationTime());
                else return (int)(o1.getFirstLongTypeTime()-o2.getFirstLongTypeTime());
            }
        };
        queue = new PriorityQueue<>(comp);

        //get first valid log entry to initialize start time
        Optional<LogEntry> firstentry = reader.parseNextLine();
        while (!firstentry.isPresent() && !reader.isEmpty()){
            firstentry = reader.parseNextLine();
        }
        //Initialize the earliest time of log entry,if no valid log entry throw exception;
        LogEntry firstLog = firstentry.orElseThrow(IllegalArgumentException::new);
        currTime  = getLongTime(firstLog.getTime());


        User firstUser = new User(firstLog.getIp(),firstLog.getTime(),getLongTime(firstLog.getTime()),currTime+period,new HashSet<>(),1);
        firstUser.getDocuments().add(firstLog.getDocument());
        map.put(firstLog.getIp(),firstUser);
        queue.offer(firstUser);
    }

    /**
     * when stream job have finished, need remove all the users data and write it to output
     */
    private static void removeAll(Writer writer){
        PriorityQueue<User> queue1 = new PriorityQueue<>(Comparator.comparing(User::getFirstLongTypeTime));
        while (!queue.isEmpty()) queue1.offer(queue.poll());
        while (!queue1.isEmpty()){
            User user = queue1.poll();
            updateUser(user);
//            System.out.println(user.toString());
            long duration = getLongTime(user.getLastCheckTime()) - getLongTime(user.getFirstCheckTime()) +1;
            writer.write(user.getIpv4(),user.getFirstCheckTime(),user.getLastCheckTime(),duration,user.getCount());
        }
    }

    /**
     * Remove expired users and write it to output when their expiration less than current time
     * @param Time the time threshold,whose expiration less or equal than it need  to be removed
     */
    private static void removeExpiration(long Time,Writer writer){
        while (!queue.isEmpty() && queue.peek().getExpirationTime() <= Time){
            User  user = queue.poll();
            // when we try to remove the user, check  whether it can be updated.
            updateUser(user);
            if(user.getExpirationTime() <= Time){
//                System.out.println(user.toString()); // 这里只打印
                long duration = getLongTime(user.getLastCheckTime()) - getLongTime(user.getFirstCheckTime())+1;
                writer.write(user.getIpv4(),user.getFirstCheckTime(),user.getLastCheckTime(),duration,user.getCount());
                map.remove(user.getIpv4());
            }
            else{
                //after update, the user survives from remove action
                queue.offer(user);
            }
        }
    }

    /**
     * Lazy-load mode, Only update the user's information when it may be removed.
     * @param user the user need to be checked.
     */
    private static void updateUser(User user){
        if(update.containsKey(user.getIpv4())){
            UpdateEntry updateEntry = update.get(user.getIpv4());
            user.setLastCheckTime(updateEntry.lastCheckTime);
            user.setExpirationTime(updateEntry.expirationTime);
            user.updateCount(updateEntry.count);

            update.remove(user.getIpv4());
        }
    }

    /**
     * Change String type time to long type time(second)
     *Attention! because the limitation of long type, I count the time to second based on the 2000 year.The time before that is invalid
     * @param date which has format of :yyyy-MM-dd HH:mm:ss
     * @return the time represented by seconds
     */
    private static long getLongTime(String date){
        String[] tokens = date.split(" ");
        String[] dateArr = tokens[0].split("-");
        String[] timeArr = tokens[1].split(":");

        long res = 86400*((Integer.parseInt(dateArr[0])-2000)*365 + Integer.parseInt(dateArr[1])*30+ Integer.parseInt(dateArr[2]));
        res += Integer.parseInt(timeArr[0])*3600 + Integer.parseInt(timeArr[1])*60 + Integer.parseInt(timeArr[2]);
        return res;
    }


}
