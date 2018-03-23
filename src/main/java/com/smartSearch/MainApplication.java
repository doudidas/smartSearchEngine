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

import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;
import java.net.UnknownHostException;
import java.util.EnumSet;

public class MainApplication extends Application<MainConfiguration> {

    public MainApplication() {}

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
        final FilterRegistration.Dynamic cors =
                environment.servlets().addFilter("CORS", CrossOriginFilter.class);

        // Configure CORS parameters
        cors.setInitParameter("allowedOrigins", "*");
        cors.setInitParameter("allowedHeaders", "X-Requested-With,Content-Type,Accept,Origin");
        cors.setInitParameter("allowedMethods", "OPTIONS,GET,PUT,POST,DELETE,HEAD");

        // Add URL mapping
        cors.addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), true, "/*");

        MongoClient mongoClient = new MongoClient(System.getenv("MONGOPATH"), 27017);
        MongoDatabase database = mongoClient.getDatabase("SmartSearchDatabase");
        environment.healthChecks().register("mongo", new MongoHealthCheck(mongoClient));
        final UserResource userResource = new UserResource(database);
        final DestinationResource destinationResource = new DestinationResource(database);
        final TopicResource topicResource = new TopicResource(database);

        environment.jersey().register(userResource);
        environment.jersey().register(topicResource);
        environment.jersey().register(destinationResource);
    }
}
