package com.insight.service;

import com.insight.model.User;

import java.io.*;
import java.util.HashSet;
import java.util.Scanner;

/**
 * Created by dustin on 2018/4/3.
 */
public class Writer {
    private BufferedWriter writer;


    public Writer(String filePath){
        File file = new File(filePath);
        if(file.exists()){
            throw new java.lang.IllegalArgumentException("The file path  already exists!");
        }
        try {
            FileWriter fileWriter = new FileWriter(file,true);
            writer = new BufferedWriter(fileWriter);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void write(String ip,String starTime,String endTime,long duration,int count){
        try {

            String line = String.format("%s,%s,%s,%d,%d",ip,starTime,endTime,duration,count);
            writer.write(line);
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void close(){
        try {
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
