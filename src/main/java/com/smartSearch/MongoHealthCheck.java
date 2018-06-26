package com.smartSearch;

import com.codahale.metrics.health.HealthCheck;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoIterable;

public class MongoHealthCheck extends HealthCheck {

    private final MongoClient mongoClient;

    MongoHealthCheck(MongoClient mongoClient) {
        super();
        this.mongoClient = mongoClient;
    }

    @Override
    protected Result check() {
        MongoDatabase db = mongoClient.getDatabase("SmartSearchDatabase");
        try {
            MongoIterable<String> allCollections = db.listCollectionNames();
            for (String collection : allCollections) {
                System.out.println("MongoDB collection: " + collection);
            }
        } catch (Exception ex) {
            return Result.unhealthy(ex.getMessage());
        }
        return Result.healthy();
    }

}
