package com.smartSearch;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
import io.dropwizard.Application;
import io.dropwizard.assets.AssetsBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

import java.net.UnknownHostException;

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
        MongoClient mongoClient = new MongoClient("localhost", 27017);
        MongoDatabase database = mongoClient.getDatabase("SmartSearchDatabase");

        environment.healthChecks().register("mongo", new MongoHealthCheck(mongoClient));
        final RecipeResource resource = new RecipeResource(database);
        final UserResource userResource = new UserResource(database);
        environment.jersey().register(resource);
        environment.jersey().register(userResource);
    }
}