package com.jayway.jsonpath.web.resource;

import com.jayway.jsonpath.web.bench.Bench;
import com.jayway.jsonpath.web.bench.Result;
import org.apache.commons.io.IOUtils;
import org.glassfish.jersey.server.mvc.Viewable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;

@Path("")
@Produces(MediaType.TEXT_HTML)
public class IndexResource {

    private static final Logger logger = LoggerFactory.getLogger(IndexResource.class);

    public final static Map<String, String> TEMPLATES = loadTemplates();

    @GET
    public Viewable get(@QueryParam("template") @DefaultValue("goessner") String template){
         return createView(TEMPLATES.get(template), "$.store.book[0].title", true, template, null);
    }

    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Viewable post(@FormParam("json") String json,
                         @FormParam("path") String path,
                         @FormParam("type") String type,
                         @FormParam("template")  String template){

        boolean value = "VALUE".equalsIgnoreCase(type);

        return createView(json, path, value, template, new Bench(json, path, value).runAll());
    }

    private Viewable createView(String json, String path, boolean value, String selectedTemplate, List<Result> results){
        Map<String, Object> res = new HashMap<String, Object>();
        res.put("results", results);
        res.put("json", json);
        res.put("path", path);
        res.put("value-checked", value?"checked":"");
        res.put("path-checked", (!value)?"checked":"");
        res.put("templates", asList(new Template("goessner", "goessner", selectedTemplate), new Template("twitter" , "twitter", selectedTemplate), new Template("webapp", "webapp", selectedTemplate), new Template("20k", "20k", selectedTemplate)));

        return new Viewable("/index", res);
    }



    private static Map<String, String> loadTemplates(){
        try {
            String goessner = IOUtils.toString(Thread.currentThread().getContextClassLoader().getResourceAsStream("json/goessner.json"), "UTF-8");
            String twitter = IOUtils.toString(Thread.currentThread().getContextClassLoader().getResourceAsStream("json/twitter.json"));
            String webapp = IOUtils.toString(Thread.currentThread().getContextClassLoader().getResourceAsStream("json/webxml.json"));
            String twentyK = IOUtils.toString(Thread.currentThread().getContextClassLoader().getResourceAsStream("json/20k.json"));

            Map<String, String> templates = new HashMap<String, String>();
            templates.put("goessner", goessner);
            templates.put("twitter", twitter);
            templates.put("webapp", webapp);
            templates.put("20k", twentyK);

            return templates;
        } catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    private static class Template {
        public final String value;
        public final String name;
        public final String selected;

        private Template(String value, String name, String selectedValue) {
            this.value = value;
            this.name = name;
            this.selected =  value.equalsIgnoreCase(selectedValue)?"selected":"";
        }
    }

}
