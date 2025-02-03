package com.be.stack.game.repository;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;

public class UserRepository {

  private static final String USER_COLLECTION = "user";

  private static final String USER_INFO_COLLECTION = "user_info";

  private final MongoClient mongoClient;

  public UserRepository(MongoClient mongoClient) {
    this.mongoClient = mongoClient;
  }


  public Future<JsonObject> fetchUserFullInfo(Long userId) {
    var command = buildFullUserInfoAggregationQuery(userId);
    return this.mongoClient.runCommand("aggregate", command);
  }

  private JsonObject buildFullUserInfoAggregationQuery(Long userId) {
    var match = new JsonObject().put("$match", new JsonObject().put("userId", userId));
    var lookup = new JsonObject().put("$lookup", new JsonObject() //
      .put("from", USER_INFO_COLLECTION) //
      .put("localField", "info") //
      .put("foreignField", "_id") //
      .put("as", "info"));
    var unwind =
      new JsonObject().put("$unwind", new JsonObject().put("path", "$info").put("preserveNullAndEmptyArrays", true));

    var pipeline = new JsonArray().add(match).add(lookup).add(unwind);

    return new JsonObject()  //
      .put("aggregate", USER_COLLECTION) //
      .put("pipeline", pipeline) //
      .put("cursor", new JsonObject());
  }

}
