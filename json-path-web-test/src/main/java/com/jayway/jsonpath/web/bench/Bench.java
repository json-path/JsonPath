package com.jayway.jsonpath.web.bench;

import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.internal.spi.json.JacksonProvider;
import com.jayway.jsonpath.spi.json.JsonProviderFactory;
import io.gatling.jsonpath.JsonPath$;
import org.boon.json.JsonParser;
import org.boon.json.ObjectMapper;
import org.boon.json.implementation.JsonParserCharArray;
import org.boon.json.implementation.ObjectMapperImpl;
import scala.collection.Iterator;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;

public class Bench {

    protected final String json;
    protected final String path;
    private final boolean value;

    public Bench(String json, String path, boolean value) {
        this.json = json;
        this.path = path;
        this.value = value;
    }

    public Result runJayway() {
        String result = null;
        String error = null;
        long time;
        Object res = null;
        long now = System.currentTimeMillis();

        try {
            if(value) {
                res = JsonPath.parse(json).read(path);
            } else {
                res = JsonPath.parse(json).readPathList(path);
            }

        } catch (Exception e){
            error = getError(e);
        } finally {
            time = System.currentTimeMillis() - now;

            if(res instanceof String) {
                result = "\"" + res + "\"";
            } else if(res instanceof Number) {
                result = res.toString();
            } else if(res instanceof Boolean){
                result = res.toString();
            } else {
                result = res != null ? JsonProviderFactory.createProvider().toJson(res) : "null";
            }
            return new Result("jayway", time, result, error);
        }
    }

    public Result runBoon() {
        String result = null;
        String error = null;
        long time;

        Iterator<Object> query = null;
        long now = System.currentTimeMillis();
        try {
            if(!value){
                throw new UnsupportedOperationException("Not supported!");
            }
            io.gatling.jsonpath.JsonPath jsonPath = JsonPath$.MODULE$.compile(path).right().get();

            JsonParser jsonParser = new JsonParserCharArray();
            Object jsonModel = jsonParser.parse(json);
            query = jsonPath.query(jsonModel);

        } catch (Exception e){
            error = getError(e);
        } finally {
            time = System.currentTimeMillis() - now;

            if(query != null) {
                List<Object> res = new ArrayList<Object>();
                while (query.hasNext()) {
                    res.add(query.next());
                }
                ObjectMapper mapper = new ObjectMapperImpl();
                result = mapper.toJson(res);
            }
            return new Result("boon", time, result, error);
        }
    }

    public Result runNebhale() {
        String result = null;
        String error = null;
        long time;
        Object res = null;
        JacksonProvider jacksonProvider = new JacksonProvider();

        long now = System.currentTimeMillis();
        try {
            if(!value){
                throw new UnsupportedOperationException("Not supported!");
            }
            com.nebhale.jsonpath.JsonPath compiled = com.nebhale.jsonpath.JsonPath.compile(path);
            res = compiled.read(json, Object.class);
        } catch (Exception e){
            error = getError(e);
        } finally {
            time = System.currentTimeMillis() - now;
            result = res!=null? jacksonProvider.toJson(res):null;
            return new Result("nebhale", time, result, error);
        }
    }

    public List<Result> runAll(){
        return asList(runJayway(), runBoon(), runNebhale());
    }

    private String getError(Exception e){
        String ex = e.getMessage();
        if(ex == null || ex.trim().isEmpty()){
            ex = "Undefined error";
        }
        return ex;
    }
}
