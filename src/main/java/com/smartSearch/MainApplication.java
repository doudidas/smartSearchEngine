package com.smartSearch;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
import io.dropwizard.Application;
import io.dropwizard.assets.AssetsBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.eclipse.jetty.servlets.CrossOriginFilter;
import resources.DefaultResource;
import resources.DestinationResource;
import resources.TopicResource;
import resources.UserResource;

import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;
import java.util.EnumSet;

public class MainApplication extends Application<MainConfiguration> {

    private MainApplication() {
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
    public void run(MainConfiguration mainConfiguration, Environment environment) {
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
            mongoClient = new MongoClient("mongo", 27017);
            database = mongoClient.getDatabase("SmartSearchDatabase");
        } catch(Exception e) {
            throw new Error("Impossible to connect to database !! " + e);
        }

        environment.healthChecks().register("mongo", new MongoHealthCheck(mongoClient));

        final UserResource userResource               = new UserResource(database);
        final TopicResource topicResource             = new TopicResource(database);
        final DefaultResource defaultResource         = new DefaultResource();
        final DestinationResource destinationResource = new DestinationResource(database);

        environment.jersey().register(destinationResource);
        environment.jersey().register(userResource);
        environment.jersey().register(topicResource);
        environment.jersey().register(defaultResource);
    }
}
