# Insight project
## This project is provided by Insight. It's kind of log parser project, which let us to identify when a user visits, calculate the duration of and number of documents requested during that visit, and then write the output to a file. We assume the data is streaming, but actually we parse data from log file. So in order for simulation, I use Scanner.next() to simulate the streaming data. 

### Realization
There are three key points about this puzzle: first is streaming, second is bigdata, third is time. 

#### stream
About stream: When the data is streaming, we need to deal the data one by one. We can't get all the data and then deal it. So I use a min heap to store the User, which is sort by expried time. The first element in the min heap is the user who has the earliest expired session. Then it will be expired, I try to update it, which means it maybe survive from the remove action, because I user lazy-load to update user's information. The lazy load method help to decrease the pressure of change the structure of min heap.

#### bigdata
When the file has million rows, it must has some "bad data", the bad data means invalid format data. if we don't deal the bad data, the procedure will stop when it process bad data. So We need a filter to deal with invalid format data. I user java regix to filter data in the Reader.java

#### time
The time format is string, but we need compare the time. That means we need change the time to int or long. So,what I have done is that I change all the time to long type in java(second). Because the number becomes too large, so I use 2000 year as base case,Only count the seconds after 2000 year.


### The Structure of my Code
- README.md 
- run.sh
- src
  - com.insight
    - common
      - ArgumentPaser.java                 (parse args)
    - model
      - LogEntry.java      
      - User.java
    - service
      - Writer.java                        (write result into file)
      - Reader.java                        (read log data fro file)
    - main
      - StreamHandler.java                 (main class)
- input
  - log1.csv
  - inactivity_period.txt
- output
  - sessionization.txt
-test
 - run_test1.sh
 - run_test2.sh
 - run_test3.sh
 - test1
   - log1.csv
   - inactivity_period.txt
   - output
     - sessionization.txt
  - test3
    - log3.csv
    - inactivity_period.txt
    - output
      - sessionization.txt
  - test3
    - log3.csv
    - inactivity_period.txt
    - output
      - sessionization.txt   
     
### Running command
```sh
java -cp "insight.jar" com.insight.StreamHandler --input="./input/log.csv" --output="./output/sessionization.txt" 
--invalidPeriod="./input/inactivity_period.txt"
```

