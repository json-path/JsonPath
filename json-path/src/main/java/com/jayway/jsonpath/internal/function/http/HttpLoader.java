package com.jayway.jsonpath.internal.function.http;

import com.jayway.jsonpath.internal.EvaluationContext;
import com.jayway.jsonpath.internal.PathRef;
import com.jayway.jsonpath.internal.function.Parameter;
import com.jayway.jsonpath.internal.function.PathFunction;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.List;

/**
 * Dirt simple http get method just to demo URL loading
 *
 * Created by mgreenwood on 12/11/15.
 */
public class HttpLoader implements PathFunction {
    @Override
    public Object invoke(String currentPath, PathRef parent, Object model, EvaluationContext ctx, List<Parameter> parameters) {
        if (parameters != null && parameters.size() == 1) {
            try {
                URL url = new URL(parameters.get(0).getCachedValue().toString());
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String line;
                StringBuffer result = new StringBuffer();
                while ((line = rd.readLine()) != null) {
                    result.append(line);
                }
                rd.close();
                Object jsonResult = ctx.configuration().jsonProvider().parse(result.toString());
                return jsonResult;
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
