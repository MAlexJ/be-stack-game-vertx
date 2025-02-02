package com.be.stack.game.repository;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;
import java.util.logging.Logger;

public class UserRepository {

  private static final Logger LOG = Logger.getLogger(UserRepository.class.getName());

  private final MongoClient mongoClient;

  public UserRepository(MongoClient mongoClient) {
    this.mongoClient = mongoClient;
  }

  public JsonObject commandFindFullUserInfoById(Long userId) {
    JsonArray pipeline =
        new JsonArray()
            .add(new JsonObject().put("$match", new JsonObject().put("userId", userId)))
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
                            .put("preserveNullAndEmptyArrays", true)));

    return new JsonObject()
        .put("aggregate", "user")
        .put("pipeline", pipeline)
        .put("cursor", new JsonObject());
  }

  public Future<JsonObject> aggregateFullUserInfoByUserId(JsonObject command) {
    return this.mongoClient.runCommand("aggregate", command);
  }
}
