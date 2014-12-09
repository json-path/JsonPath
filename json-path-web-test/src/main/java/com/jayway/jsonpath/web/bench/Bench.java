package com.jayway.jsonpath.web.bench;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;
import com.jayway.jsonpath.spi.json.JacksonJsonProvider;
import io.gatling.jsonpath.JsonPath$;
import org.boon.json.JsonParser;
import org.boon.json.ObjectMapper;
import org.boon.json.implementation.JsonParserCharArray;
import org.boon.json.implementation.ObjectMapperImpl;
import scala.collection.Iterator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Bench {

    protected final String json;
    protected final String path;
    private final boolean optionAsValues;
    private final boolean flagWrap;
    private final boolean flagSuppress;
    private final boolean flagNullLeaf;
    private final boolean flagRequireProps;

    public Bench(String json, String path, boolean optionAsValues, boolean flagWrap, boolean flagSuppress, boolean flagNullLeaf, boolean flagRequireProps) {
        this.json = json;
        this.path = path;
        this.optionAsValues = optionAsValues;
        this.flagWrap = flagWrap;
        this.flagSuppress = flagSuppress;
        this.flagNullLeaf = flagNullLeaf;
        this.flagRequireProps = flagRequireProps;
    }

    public Result runJayway() {
        String result = null;
        String error = null;
        long time;
        Object res = null;


        Configuration configuration = Configuration.defaultConfiguration();
        if(flagWrap){
            configuration = configuration.addOptions(Option.ALWAYS_RETURN_LIST);
        }
        if(flagSuppress){
            configuration = configuration.addOptions(Option.SUPPRESS_EXCEPTIONS);
        }
        if (!optionAsValues) {
            configuration = configuration.addOptions(Option.AS_PATH_LIST);
        }
        if(flagNullLeaf){
            configuration = configuration.addOptions(Option.DEFAULT_PATH_LEAF_TO_NULL);
        }
        if(flagRequireProps){
            configuration = configuration.addOptions(Option.REQUIRE_PROPERTIES);
        }

        long now = System.currentTimeMillis();
        try {
            res = JsonPath.using(configuration).parse(json).read(path);
        } catch (Exception e) {
            error = getError(e);
        } finally {
            time = System.currentTimeMillis() - now;

            if (res instanceof String) {
                result = "\"" + res + "\"";
            } else if (res instanceof Number) {
                result = res.toString();
            } else if (res instanceof Boolean) {
                result = res.toString();
            } else {
                result = res != null ? Configuration.defaultConfiguration().jsonProvider().toJson(res) : "null";
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
            if (!optionAsValues) {
                throw new UnsupportedOperationException("Not supported!");
            }
            io.gatling.jsonpath.JsonPath jsonPath = JsonPath$.MODULE$.compile(path).right().get();

            JsonParser jsonParser = new JsonParserCharArray();
            Object jsonModel = jsonParser.parse(json);
            query = jsonPath.query(jsonModel);

        } catch (Exception e) {
            error = getError(e);
        } finally {
            time = System.currentTimeMillis() - now;

            if (query != null) {
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
        JacksonJsonProvider jacksonProvider = new JacksonJsonProvider();

        long now = System.currentTimeMillis();
        try {
            if (!optionAsValues) {
                throw new UnsupportedOperationException("Not supported!");
            }
            com.nebhale.jsonpath.JsonPath compiled = com.nebhale.jsonpath.JsonPath.compile(path);
            res = compiled.read(json, Object.class);
        } catch (Exception e) {
            error = getError(e);
        } finally {
            time = System.currentTimeMillis() - now;
            result = res != null ? jacksonProvider.toJson(res) : null;
            return new Result("nebhale", time, result, error);
        }
    }

    public Map<String, Result> runAll() {
        Map<String, Result> res = new HashMap<String, Result>();
        res.put("jayway", runJayway());
        res.put("boon", runBoon());
        res.put("nebhale", runNebhale());
        return res;
    }

    private String getError(Exception e) {
        String ex = e.getMessage();
        if (ex == null || ex.trim().isEmpty()) {
            ex = "Undefined error";
        }
        return ex;
    }
}
