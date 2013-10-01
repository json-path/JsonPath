package com.jayway.jsonpath.web.boot;

import static org.eclipse.jetty.servlet.ServletContextHandler.NO_SESSIONS;

import java.io.IOException;

import com.jayway.jsonpath.web.resource.IndexResource;
import com.jayway.jsonpath.web.resource.StaticResource;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.mvc.mustache.MustacheMvcFeature;
import org.glassfish.jersey.servlet.ServletContainer;



public class Main {

    public static void main(String[] args) throws Exception {
        Server s = new Server();

        s.setConnectors(new Connector[] { createConnector(s) });

        ServletContextHandler context = new ServletContextHandler(NO_SESSIONS);
        context.setContextPath("/");
        ServletHolder servletHolder = new ServletHolder(createJerseyServlet());
        servletHolder.setInitOrder(1);
        context.addServlet(servletHolder, "/*");
        s.setHandler(context);

        s.start();
        s.join();
    }

    private static ServerConnector createConnector(Server s){
        ServerConnector connector = new ServerConnector(s);
        connector.setHost("localhost");
        connector.setPort(8080);
        return connector;
    }

    private static ServletContainer createJerseyServlet() throws IOException {
        ResourceConfig resourceConfig = new ResourceConfig();
        resourceConfig.property(MustacheMvcFeature.TEMPLATE_BASE_PATH, "templates");

        resourceConfig.register(MustacheMvcFeature.class);
        resourceConfig.register(JacksonFeature.class);

        resourceConfig.register(new IndexResource());
        resourceConfig.register(new StaticResource());

        return new ServletContainer(resourceConfig);
    }
}
