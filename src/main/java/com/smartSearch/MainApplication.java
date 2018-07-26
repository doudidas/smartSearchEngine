package com.smartSearch;

import java.util.EnumSet;

import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoDatabase;

import org.eclipse.jetty.servlets.CrossOriginFilter;

import io.dropwizard.Application;
import io.dropwizard.assets.AssetsBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import resources.DestinationResource;
import resources.HelloResource;
import resources.TopicResource;
import resources.UserResource;

public class MainApplication extends Application<MainConfiguration> {

    private MainApplication() {
    }

    public static void main(String[] args) throws Exception {
        new MainApplication().run(args);
    }

    @Override
    public String getName() {
        return "Spacelama-api";
    }

    @Override
    public void initialize(Bootstrap<MainConfiguration> bootstrap) {

        bootstrap.addBundle(new AssetsBundle("/assets/", "/"));
    }

    @Override
    public void run(MainConfiguration mainConfiguration, Environment environment) {
        // Enable CORS headers
        final FilterRegistration.Dynamic cors = environment.servlets().addFilter("CORS", CrossOriginFilter.class);

        // Configure CORS parameters
        cors.setInitParameter("allowedOrigins", "*");
        cors.setInitParameter("allowedHeaders", "X-Requested-With,Content-Type,Accept,Origin");
        cors.setInitParameter("allowedMethods", "OPTIONS,GET,PUT,POST,DELETE,HEAD");

        // Add URL mapping
        cors.addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), true, "/*");
        MongoClient mongoClient;
        MongoDatabase database;
        String hostname = (System.getenv("CONTAINER") != null) ? "mongo" : "localhost";


        mongoClient = new MongoClient(new ServerAddress(hostname, 27017), MongoClientOptions.builder()
                .socketTimeout(1000)
                .minHeartbeatFrequency(25)
                .heartbeatSocketTimeout(1000)
                .socketTimeout(1000)
                .connectTimeout(1000)
                .serverSelectionTimeout(1000)
                .build());

        database = mongoClient.getDatabase("SmartSearchDatabase");

        environment.healthChecks().register("mongo", new MongoHealthCheck(mongoClient));

        final UserResource userResource               = new UserResource(database);
        final HelloResource helloResource             = new HelloResource();
        final TopicResource topicResource             = new TopicResource(database);
        final DestinationResource destinationResource = new DestinationResource(database);

        environment.jersey().register(destinationResource);
        environment.jersey().register(userResource);
        environment.jersey().register(topicResource);
        environment.jersey().register(helloResource);
    }
}