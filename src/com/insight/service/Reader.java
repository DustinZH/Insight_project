package com.insight.service;

import com.insight.model.LogEntry;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;
import java.util.Scanner;
import java.util.regex.Pattern;

/**
 * Created by dustin on 2018/4/3.
 */
public class Reader {
    private  Scanner scanner;
    private  final String IPv4_FORMATE_CHEK = "^((\\d{1,2})|([1]\\d{2})|([2][0-5]{2}))\\." +
            "((\\d{1,2})|([1]\\d{2})|([2][0-5]{2}))\\." +
            "((\\d{1,2})|([1]\\d{2})|([2][0-5]{2}))\\." +
            "[a-z]{3}$";

    private final SimpleDateFormat DATE_FORMAT_CHECK = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


    public Reader(String filePath){
        try {
            this.scanner = new Scanner(new FileInputStream(filePath));
        } catch (FileNotFoundException e) {
            throw new java.lang.IllegalArgumentException("file doesn't exist");
        }
    }

    public Optional<LogEntry> parseNextLine(){
        if(scanner.hasNext()){
            String line = scanner.next();
            String[] tokens = line.split(",");

            Optional<String> ip = parseIPV4(tokens[0]);
            Optional<String> time = parseTime(tokens[1],tokens[2]);

            if(!ip.isPresent() || !time.isPresent()){
//                String error = "";
//                if (!ip.isPresent()) error = ip.orElse(String.format("Error IP format: %s  ",tokens[0]));
//                if(! ip.isPresent()) error += time.orElse(String.format("Error IP format: %s %s ",tokens[1],tokens[2]));
//                System.out.println(error);
                return Optional.empty();
            }
            String document = tokens[4]+"#"+tokens[5]+"#"+tokens[6];
            LogEntry entry = new LogEntry(ip.get(),time.get(),document);
            return Optional.of(entry);
        }
        else return Optional.empty();
    }

    private Optional<String> parseIPV4(String ip){
        if(ip == null) return Optional.empty();

        if(Pattern.matches(IPv4_FORMATE_CHEK,ip)){
            return Optional.of(ip);
        }
        else return Optional.empty();
    }

    private Optional<String> parseTime(String date,String time){
        if(date == null|| time == null) return Optional.empty();
        String t = date+ " "+time;
        try {
            Date d = DATE_FORMAT_CHECK.parse(t);
            return Optional.of(DATE_FORMAT_CHECK.format(d));
        }catch (ParseException ex){
            return Optional.empty();
        }
    }

    public boolean isEmpty(){
        return !scanner.hasNext();
    }

    public void close(){
        scanner.close();
    }


}
