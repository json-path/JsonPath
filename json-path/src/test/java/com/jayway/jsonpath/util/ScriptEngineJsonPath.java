package com.jayway.jsonpath.util;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

/**
 * Created by IntelliJ IDEA.
 * User: kallestenflo
 * Date: 11/8/11
 * Time: 1:01 PM
 */
public class ScriptEngineJsonPath {

    private static ScriptEngineManager manager = new ScriptEngineManager();
    private static ScriptEngine engine = manager.getEngineByName("JavaScript");

    private static final String JSON_PATH_SCRIPT = readScript("jsonpath-0.8.0.js");
    private static final String JSON_SCRIPT = readScript("json.js");
    private static final String WRAPPER_SCRIPT = readScript("wrapper.js");

    static {
        try {
            engine.eval(JSON_PATH_SCRIPT);
            engine.eval(JSON_SCRIPT);
            engine.eval(WRAPPER_SCRIPT);
        } catch (ScriptException e) {
            throw new RuntimeException(e);
        }
    }

    public static String eval(String json, String path) throws Exception {
        Invocable inv = (Invocable) engine;
        Object obj = engine.get("WRAPPER");
        return (String)inv.invokeMethod(obj, "jsonPath", json, path);
    }


    private static String readScript(String script) {
        InputStream is = null;
        try {
            is = ScriptEngineJsonPath.class.getClassLoader().getSystemResourceAsStream("js/" + script);

            return new Scanner(is).useDelimiter("\\A").next();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }


}
