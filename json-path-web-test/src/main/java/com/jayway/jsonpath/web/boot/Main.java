package com.jayway.jsonpath.web.boot;

import com.jayway.jsonpath.web.resource.ApiResource;
import com.jayway.jsonpath.web.resource.StaticResource;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;

import java.io.IOException;

import static org.eclipse.jetty.servlet.ServletContextHandler.NO_SESSIONS;



public class Main {

    public static void main(String[] args) throws Exception {
        int port = 8080;
        if(args.length > 0){
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException nfe) {
                System.out.println("Invalid usage! Port argument must be an integer (if not supplied " + port + " is used)");
            }
        }
        System.out.println("Server started on port: " + port);

        Server server = new Server();

        server.setConnectors(new Connector[]{createConnector(server, port)});

        ServletContextHandler context = new ServletContextHandler(NO_SESSIONS);
        context.setContextPath("/");
        ServletHolder servletHolder = new ServletHolder(createJerseyServlet());
        servletHolder.setInitOrder(1);
        context.addServlet(servletHolder, "/*");
        server.setHandler(context);

        server.start();
        server.join();
    }

    private static ServerConnector createConnector(Server s, int port){
        ServerConnector connector = new ServerConnector(s);
        connector.setHost("localhost");
        connector.setPort(port);
        return connector;
    }

    private static ServletContainer createJerseyServlet() throws IOException {
        ResourceConfig resourceConfig = new ResourceConfig();
        resourceConfig.register(JacksonFeature.class);

        resourceConfig.register(new ApiResource());
        resourceConfig.register(new StaticResource());

        return new ServletContainer(resourceConfig);
    }
}
