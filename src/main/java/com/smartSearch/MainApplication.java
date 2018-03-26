package com.smartSearch;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
import io.dropwizard.Application;
import io.dropwizard.assets.AssetsBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.eclipse.jetty.servlets.CrossOriginFilter;
import resources.DestinationResource;
import resources.TopicResource;
import resources.UserResource;
import resources.DefaultResource;
import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;
import java.net.UnknownHostException;
import java.util.EnumSet;
import java.lang.System;
import java.lang.Error;

public class MainApplication extends Application<MainConfiguration> {

    public MainApplication() {
    }

    public static void main(String[] args) throws Exception {
        new MainApplication().run(args);
    }

    @Override
    public String getName() {
        return "recipes";
    }

    @Override
    public void initialize(Bootstrap<MainConfiguration> bootstrap) {

        bootstrap.addBundle(new AssetsBundle("/assets/", "/"));
    }

    @Override
    public void run(MainConfiguration mainConfiguration, Environment environment) throws UnknownHostException {
        // Enable CORS headers
        final FilterRegistration.Dynamic cors = environment.servlets().addFilter("CORS", CrossOriginFilter.class);

        // Configure CORS parameters
        cors.setInitParameter("allowedOrigins", "*");
        cors.setInitParameter("allowedHeaders", "X-Requested-With,Content-Type,Accept,Origin");
        cors.setInitParameter("allowedMethods", "OPTIONS,GET,PUT,POST,DELETE,HEAD");

        // Add URL mapping
        cors.addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), true, "/*");
        String mongoAdress = (System.getenv("MONGO_PORT_27017_TCP_ADDR") == null) ? "localhost": System.getenv("MONGO_PORT_27017_TCP_ADDR");

        MongoClient mongoClient;
        MongoDatabase database;
        
        try {
            mongoClient = new MongoClient(mongoAdress, 27017);
            database = mongoClient.getDatabase("SmartSearchDatabase");
        } catch(Exception e) {
            throw new Error("Impossible to connect to database !! " + e);
        }


        environment.healthChecks().register("mongo", new MongoHealthCheck(mongoClient));
        final UserResource userResource = new UserResource(database);
        final DestinationResource destinationResource = new DestinationResource(database);
        final TopicResource topicResource = new TopicResource(database);

        environment.jersey().register(destinationResource);
        environment.jersey().register(userResource);
        environment.jersey().register(topicResource);
        try {
            final DefaultResource defaultResource = new DefaultResource();
            environment.jersey().register(defaultResource);
        } catch (Exception e) {
            System.out.println("Fail to load Default Resource " + e);
        }
    }
}
