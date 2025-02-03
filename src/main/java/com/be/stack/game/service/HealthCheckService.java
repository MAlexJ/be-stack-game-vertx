package com.be.stack.game.service;

import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.healthchecks.HealthCheckHandler;
import io.vertx.ext.healthchecks.Status;
import io.vertx.ext.mongo.MongoClient;

public class HealthCheckService {

  private HealthCheckService() {
  }

  public static HealthCheckHandler createHealthCheckHandler(MongoClient mongoClient, Vertx vertx) {
    HealthCheckHandler healthCheckHandler = HealthCheckHandler.create(vertx);

    // Add a health check for MongoDB connection
    healthCheckHandler.register("mongo", promise -> checkMongoDb(mongoClient, promise));

    // Add other health checks as needed (e.g., for the server itself)
    healthCheckHandler.register("server", future -> future.complete(Status.OK()));

    return healthCheckHandler;
  }

  private static void checkMongoDb(MongoClient mongoClient, Promise<Status> future) {
    JsonObject pingCommand = new JsonObject().put("ping", 1);  // Correct "ping" command for MongoDB
    mongoClient.runCommand("ping", pingCommand, res -> {
      if (res.succeeded()) {
        future.complete(Status.OK());
      } else {
        future.fail("MongoDB is down: " + res.cause().getMessage());
      }
    });
  }
}
