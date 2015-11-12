package com.jayway.jsonpath.internal;

import org.junit.Test;

public class UtilsTest {


    @Test
    public void strings_can_be_escaped() {

        String str =  "it\\\\";
        System.out.println(Utils.unescape(str));
        System.out.println(Utils.escape(str, true));

    }

}
