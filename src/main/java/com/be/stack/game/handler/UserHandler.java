package com.be.stack.game.handler;

import com.be.stack.game.dto.UserDto;
import com.be.stack.game.repository.UserRepository;

import io.vertx.core.Future;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.ext.web.RoutingContext;
import java.util.Optional;
import java.util.function.Function;
import java.util.logging.Logger;

public class UserHandler {

  private static final Logger LOG = Logger.getLogger(UserHandler.class.getName());

  private final UserRepository userRepository;

  public UserHandler(MongoClient mongoClient) {
    this.userRepository = new UserRepository(mongoClient);
  }

  public void findAllUserInfoByUserId(RoutingContext ctx) {
    ctx.vertx().executeBlocking(() -> {
      Long userId = Optional.ofNullable(ctx.get("userId")).map(String::valueOf).map(Long::parseLong).orElseThrow();
      JsonObject command = userRepository.commandFindFullUserInfoById(userId);
      return userRepository.aggregateFullUserInfoByUserId(command) //
        .map(mapJsonObjectToUserDto()) //
        .onSuccess(ctx::json) //
        .onFailure(err -> ctx.fail(400, err));
    }, false);
  }


  private Function<JsonObject, UserDto> mapJsonObjectToUserDto() {
    return jsonObject -> {
      var jsonArray = jsonObject.getJsonObject("cursor").getJsonArray("firstBatch");
      JsonObject userJsonObject = jsonArray.getJsonObject(0);
      return userJsonObject.mapTo(UserDto.class);
    };
  }


  public void findAllUserInfoByUserIdOld(RoutingContext context) {
    Long userId = Optional.ofNullable(context.get("userId")).map(String::valueOf).map(Long::parseLong).orElseThrow();
    HttpServerResponse response = context.response();

    JsonObject command = userRepository.commandFindFullUserInfoById(userId);
    Future<JsonObject> jsonObjectFuture = userRepository.aggregateFullUserInfoByUserId(command);

    jsonObjectFuture.map(jsonObject -> {
        JsonArray result = jsonObject.getJsonObject("cursor").getJsonArray("firstBatch");

        JsonObject object = result.getJsonObject(0);

        UserDto userDto = object.mapTo(UserDto.class);

        LOG.info("userDto: " + userDto);

//        vertx.executeBlocking(promise -> {
//          GameDataEntity gameData = gameDataRepository.findById(123); // Блокирующая операция
//          promise.complete(gameData);
//        }).onSuccess(gameData -> {
//          ctx.json(gameData);
//        }).onFailure(err -> {
//          ctx.fail(500, err);
//        });

        return object; //
      }).onSuccess(json -> response.setStatusCode(200).end(json.encodePrettily()))
      .onFailure(t -> response.setStatusCode(400).end(t.getMessage()));
  }
}
