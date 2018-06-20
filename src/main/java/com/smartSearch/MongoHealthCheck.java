package com.smartSearch;

import com.codahale.metrics.health.HealthCheck;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientException;

public class MongoHealthCheck extends HealthCheck {

    private final MongoClient mongoClient;

    MongoHealthCheck(MongoClient mongoClient) {
        super();
        this.mongoClient = mongoClient;
    }

    @Override
    protected Result check() {

        try {
            mongoClient.getDatabase("SmartSearchDatabase").listCollectionNames();
        } catch (MongoClientException ex) {
            return Result.unhealthy(ex.getMessage());
        }
        return Result.healthy();
    }

}
