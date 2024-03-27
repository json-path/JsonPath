package com.jayway.jsonpath;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author skwqyg
 * @Created on 2022 09 2022/9/10 16:36
 */
public class Issue_857 extends BaseTest{

    @Test
    public void test(){
        Collection values = JsonPath.parse("[{\"key\":\"first value\"},{\"key\":\"second value\"}]")
                .read("$..[*].key", java.util.Collection.class);
        Assert.assertEquals(2,values.size());
        Set<String> expect = new HashSet<>();
        expect.add("first value");
        expect.add("second value");
        Assert.assertTrue(expect.containsAll(values));
        System.out.println(values);
    }

}
