package com.be.stack.game.handler;

import com.be.stack.game.dto.UserDto;
import com.be.stack.game.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.vertx.core.Future;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.ext.web.RoutingContext;
import java.util.Optional;
import java.util.logging.Logger;

public class UserHandler {

  private static final Logger LOG = Logger.getLogger(UserHandler.class.getName());

  private final UserRepository userRepository;

  public UserHandler(MongoClient mongoClient) {
    this.userRepository = new UserRepository(mongoClient);
  }

  public void findAllUserInfoByUserId(RoutingContext context) {

    ObjectMapper mapper = new ObjectMapper();


    Long userId = Optional.ofNullable(context.get("userId")).map(String::valueOf).map(Long::parseLong).orElseThrow();
    HttpServerResponse response = context.response();

    JsonObject command = userRepository.commandFindFullUserInfoById(userId);
    Future<JsonObject> jsonObjectFuture = userRepository.aggregateFullUserInfoByUserId(command);

    jsonObjectFuture.map(jsonObject -> {
        JsonArray result = jsonObject.getJsonObject("cursor").getJsonArray("firstBatch");

        JsonObject object = result.getJsonObject(0);

        UserDto userDto
          = object.mapTo(UserDto.class);

        System.out.printf("");

        return context.json(userDto);
//          return "Hello cat";
      }).onSuccess(r -> response.setStatusCode(200))
      .onFailure(t -> response.setStatusCode(400).end(t.getMessage()));
  }
}
