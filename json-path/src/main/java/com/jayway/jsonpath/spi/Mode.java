package com.jayway.jsonpath.spi;



/**
 * Created by IntelliJ IDEA.
 * User: kallestenflo
 * Date: 11/8/11
 * Time: 7:22 PM
 */
public enum Mode {

    SLACK(-1),
    STRICT(1);

    private final int mode;

    Mode(int mode) {
        this.mode = mode;
    }

    public int intValue(){
        return mode;
    }

    public Mode parse(int mode){
        if(mode == -1){
            return SLACK;
        } else if(mode == 1){
            return STRICT;
        } else {
            throw new IllegalArgumentException("Mode " + mode + " not supported");
        }
    }


}
