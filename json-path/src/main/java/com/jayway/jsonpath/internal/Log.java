package com.jayway.jsonpath.internal;

import org.apache.commons.lang3.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * User: kalle
 * Date: 8/28/13
 * Time: 10:23 AM
 */
public final class Log {

    private Log() {
    }

    private static boolean enabled = false;

    public static void enableDebug(){
        enabled = true;
    }

    public static boolean isDebugEnabled(){
        return enabled;
    }

    public static void debug(String msg, Object... args){
        if(enabled){

            int argCount = StringUtils.countMatches(msg, "{}");

            if(!(argCount == args.length)){
                throw new RuntimeException("Invalid debug statement.");
            }

            StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();

            String cls = stackTraceElements[2].getClassName();

            msg = msg.replaceFirst(Pattern.quote("{}"), "%s");

            msg = String.format(msg, args);

            System.out.println("DEBUG [" + Thread.currentThread().getName() + "] " + cls + "  " +  msg);
        }

    }

}
