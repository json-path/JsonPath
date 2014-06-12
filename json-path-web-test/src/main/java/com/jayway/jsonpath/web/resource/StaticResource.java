package com.jayway.jsonpath.web.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import java.io.InputStream;

@Path("/")
public class StaticResource {

    @GET
    public InputStream index(@Context UriInfo uriInfo){
        return Thread.currentThread().getContextClassLoader().getResourceAsStream("html/index.html");
    }

    @GET
    @Path("{resource}")
    public InputStream html(@Context UriInfo uriInfo){
        return Thread.currentThread().getContextClassLoader().getResourceAsStream("html/" + uriInfo.getPathParameters().getFirst("resource"));
    }

    @GET
    @Path("fonts/{resource}")
    public InputStream font(@Context UriInfo uriInfo){
        return Thread.currentThread().getContextClassLoader().getResourceAsStream("fonts/" + uriInfo.getPathParameters().getFirst("resource"));
    }

    @GET
    @Path("js/{resource}")
    public InputStream js(@Context UriInfo uriInfo){
        return Thread.currentThread().getContextClassLoader().getResourceAsStream("js/" + uriInfo.getPathParameters().getFirst("resource"));
    }

    @GET
    @Path("css/{resource}")
    public InputStream css(@Context UriInfo uriInfo){
        return Thread.currentThread().getContextClassLoader().getResourceAsStream("css/" + uriInfo.getPathParameters().getFirst("resource"));
    }

    @GET
    @Path("json/{resource}")
    @Produces(MediaType.APPLICATION_JSON)
    public InputStream getTemplate(@Context UriInfo uriInfo){
        if(uriInfo.getPathParameters().getFirst("resource").contains("blank")){
            return null;
        }
        return Thread.currentThread().getContextClassLoader().getResourceAsStream("json/" + uriInfo.getPathParameters().getFirst("resource"));
    }
}
