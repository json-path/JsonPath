package com.jayway.jsonpath.web.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;

import java.io.InputStream;

@Path("/static/")
public class StaticResource {

    @GET
    @Path("fonts/{resource}")
    //@Produces("text/javascript")
    public InputStream getFonts(@Context UriInfo uriInfo){
        return Thread.currentThread().getContextClassLoader().getResourceAsStream("fonts/" + uriInfo.getPathParameters().getFirst("resource"));
    }

    @GET
    @Path("js/{resource}")
    @Produces("text/javascript")
    public InputStream getJs(@Context UriInfo uriInfo){
        return Thread.currentThread().getContextClassLoader().getResourceAsStream("js/" + uriInfo.getPathParameters().getFirst("resource"));
    }

    @GET
    @Path("css/{resource}")
    @Produces("text/css")
    public InputStream getCss(@Context UriInfo uriInfo){
        return Thread.currentThread().getContextClassLoader().getResourceAsStream("css/" + uriInfo.getPathParameters().getFirst("resource"));
    }
}
