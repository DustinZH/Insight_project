# Insight project
## This project is provided by Insight. It's kind of log parser project, which let us to identify when a user visits, calculate the duration of and number of documents requested during that visit, and then write the output to a file. We assume the data is streaming, but actually we parse data from log file. So in order for simulation, I use Scanner.next() to simulate the streaming data. 

### The Structure of my Code

    ├── README.md 
    ├── run.sh
    ├── src
    │   └── com.insight
    │                 ├──StreamHandler.java            (the main class)
    │                 ├──common
    |          │      ├──ArgumentParser.java  (used to parse  args)
    │                 │
    │                 ├── model
    │                 │       └── LogEntry.java
    │                 │       └── User.java      
    │                 └── service
    │                           └── Writer.java   (write result into file)
    │                           └── Reader.java   (read data from log file)     
    ├── input
    │   └── inactivity_period.txt
    │   └── log.csv
    ├── output
    |   └── sessionization.txt
    ├── test
        └── run_test1.sh
        └── run_test2.sh
        └── run_test3.sh
        └── test1
        │    └── log1.csv
        │    └── inactivity_period.txt
        │    |__ output
        │             └── sessionization.txt
        └── test2
        │    └── log2.csv
        │    └── inactivity_period.txt
        │    |__ output
        │             └── sessionization.txt
        └── test3
            └── log3.csv
            └── inactivity_period.txt
            |__ output
                     └── sessionization.txt                    