package com.insight.common;

import java.util.Arrays;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Created by dustin on 2018/4/4.
 */
public class ArgumentParser {
    private static final Pattern ARGU_NAME_CHECK = Pattern.compile("^[-]{2}(\\w+)[=](.+)$");


    public static Map<String,String> parseArgument(String... args){
        if(args == null){
            throw new java.lang.IllegalArgumentException();
        }
        Map<String,String> map = Arrays.asList(args).stream().map(ARGU_NAME_CHECK ::matcher).filter(Matcher :: find)
                                                    .collect(Collectors.toMap(matcher -> matcher.group(1),matcher -> matcher.group(2)));
        if(!map.containsKey("input") || !map.containsKey("output") || !map.containsKey("invalidPeriod")){
            throw new java.lang.IllegalArgumentException("input format must has --input=,--invalidPeriod=,--output=");
        }
        return map;
    }
}
