package com.be.stack.game.handler;

import static com.be.stack.game.handler.AuthFilterHandler.TG_INIT_DATA_USER_ID;

import com.be.stack.game.dto.UserDto;
import com.be.stack.game.repository.UserRepository;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.ext.web.RoutingContext;
import java.util.NoSuchElementException;
import java.util.Optional;

public class UserHandler {

  private final UserRepository userRepository;

  public UserHandler(MongoClient mongoClient) {
    this.userRepository = new UserRepository(mongoClient);
  }

  public void findAllUserInfoByUserId(RoutingContext ctx) {
    Future.succeededFuture(extractUserId(ctx)) //
      .compose(userRepository::fetchUserFullInfo) //
      .map(this::extractUserDtoFromJson) //
      .onSuccess(ctx::json) //
      .onFailure(t -> ctx.response().setStatusCode(400).end(t.getMessage()));
  }

  private Long extractUserId(RoutingContext ctx) {
    return Optional.ofNullable(ctx.get(TG_INIT_DATA_USER_ID)) //
      .map(String::valueOf) //
      .map(Long::parseLong) //
      .orElseThrow(() -> new IllegalArgumentException("User ID is missing"));
  }

  private UserDto extractUserDtoFromJson(JsonObject jsonObject) {
    var users = jsonObject.getJsonObject("cursor").getJsonArray("firstBatch");

    if (users == null || users.isEmpty()) {
      throw new NoSuchElementException("User not found");
    }

    return users.getJsonObject(0).mapTo(UserDto.class);
  }
}
