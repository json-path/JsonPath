package com.jayway;

import org.junit.Test;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

/**
 * Created by IntelliJ IDEA.
 * User: kallestenflo
 * Date: 2/3/11
 * Time: 8:52 PM
 */
public class ScriptTest {


    @Test
    public void script() throws Exception {
        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByName("js");

        System.out.println(engine.eval("1 === 2"));
        System.out.println(engine.eval("'APA' === 'APA'").getClass());
    }
}
