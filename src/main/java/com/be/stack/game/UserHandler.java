package com.be.stack.game;

import io.vertx.core.http.HttpHeaders;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.ext.web.RoutingContext;
import java.util.logging.Logger;

public class UserHandler {

  private static final Logger LOG = Logger.getLogger(UserHandler.class.getName());

  private final MongoClient mongoClient;

  public UserHandler(MongoClient mongoClient) {
    this.mongoClient = mongoClient;
  }

  public void getUsers(RoutingContext context) {
    JsonArray pipeline =
        new JsonArray()
            .add(new JsonObject().put("$match", new JsonObject().put("userId", 6105463387L)))
            .add(
                new JsonObject()
                    .put(
                        "$lookup",
                        new JsonObject()
                            .put("from", "user_info")
                            .put("localField", "info")
                            .put("foreignField", "_id")
                            .put("as", "info")))
            .add(
                new JsonObject()
                    .put(
                        "$unwind",
                        new JsonObject()
                            .put("path", "$info")
                            // Allow info can be null
                            .put("preserveNullAndEmptyArrays", true)));

    JsonObject command =
        new JsonObject()
            .put("aggregate", "user")
            .put("pipeline", pipeline)
            .put("cursor", new JsonObject());

    mongoClient.runCommand(
        "aggregate",
        command,
        res -> {
          if (res.succeeded()) {

            LOG.info("Successfully executed aggregate" + res.result());

            JsonArray result = res.result().getJsonObject("cursor").getJsonArray("firstBatch");

            if (result.isEmpty()) {
              context.response().setStatusCode(404).end("User not found 2");
            } else {

              context
                  .response()
                  .putHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                  .end(result.getJsonObject(0).encode());
            }
          } else {
            context.response().setStatusCode(404).end("User not found 2");
          }
        });
  }

  public void getUserById(RoutingContext context) {
    String userId = context.pathParam("userId");
    mongoClient.findOne(
        "users",
        new JsonObject().put("userId", userId),
        null,
        res -> {
          if (res.succeeded() && res.result() != null) {
            context
                .response()
                .putHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                .end(res.result().encode());
          } else {
            context.response().setStatusCode(404).end("User not found");
          }
        });
  }

  public void createUser(RoutingContext context) {
    context
        .request()
        .bodyHandler(
            body -> {
              JsonObject user = body.toJsonObject();
              mongoClient.insert(
                  "users",
                  user,
                  res -> {
                    if (res.succeeded()) {
                      context
                          .response()
                          .setStatusCode(201)
                          .putHeader("Content-Type", "application/json")
                          .end(new JsonObject().put("id", res.result()).encode());
                    } else {
                      context.fail(500);
                    }
                  });
            });
  }
}
