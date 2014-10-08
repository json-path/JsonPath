package com.jayway.jsonpath.web.resource;

import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.web.bench.Bench;
import com.jayway.jsonpath.web.bench.Result;
import net.minidev.json.JSONStyle;
import net.minidev.json.JSONValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

@Path("/")
@Produces(MediaType.TEXT_HTML)
public class ApiResource {

    private static final Logger logger = LoggerFactory.getLogger(ApiResource.class);

    static {
        JSONValue.COMPRESSION = JSONStyle.LT_COMPRESS;
    }

    @GET
    @Path("/info")
    @Produces(MediaType.APPLICATION_JSON)
    public Response info() {
        Map<String, String> result = new HashMap<String, String>();
        try {
            ResourceBundle resource = ResourceBundle.getBundle("build-info");
            result.put("version", resource.getString("version"));
            result.put("timestamp", resource.getString("timestamp"));
        } catch (Exception e){
            result.put("version", "LOCAL");
            result.put("timestamp", "NOW");
        }
        return Response.ok(result).build();
    }

    @GET
    @Path("/validate")
    @Produces(MediaType.APPLICATION_JSON)
    public Response validate(@QueryParam("path") String path) {
        int result = -1;
        try {
            JsonPath compiled = JsonPath.compile(path);
            result = compiled.isDefinite() ? 0 : 1;
        } catch (Exception e) {
        }
        return Response.ok(Collections.singletonMap("result", result)).build();
    }


    @POST
    @Path("/eval")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTemplate(@FormParam("json") String json,
                                @FormParam("path") String path,
                                @FormParam("type") String type,
                                @FormParam("flagWrap") boolean flagWrap,
                                @FormParam("flagNullLeaf") boolean flagNullLeaf,
                                @FormParam("flagSuppress") boolean flagSuppress,
                                @FormParam("flagRequireProps") boolean flagRequireProps) {

        boolean value = "VALUE".equalsIgnoreCase(type);

        Map<String, Result> resultMap = new Bench(json, path, value, flagWrap, flagSuppress, flagNullLeaf, flagRequireProps).runAll();

        return Response.ok(resultMap).build();
    }


}
