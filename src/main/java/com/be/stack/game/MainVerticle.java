package com.be.stack.game;

import com.be.stack.game.handler.AuthFilterHandler;
import com.be.stack.game.handler.FailureHandle;
import com.be.stack.game.handler.UserHandler;
import io.github.cdimascio.dotenv.Dotenv;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.ext.web.Router;
import java.util.Optional;
import java.util.logging.Logger;

public class MainVerticle extends AbstractVerticle {

  private static final Logger LOG = Logger.getLogger(MainVerticle.class.getName());

  private final Integer port =
      Optional.of(Dotenv.load().get("SERVER_PORT", "8080"))
          .map(Integer::parseInt)
          .orElseThrow(() -> new RuntimeException("Server port not set"));

  private MongoClient initMongoClient() {
    Dotenv dotenv = Dotenv.load();
    String mongoUri = dotenv.get("MONGO_URI", "mongodb://localhost:27017");
    String mongoDb = dotenv.get("MONGO_DB", "mongodb");
    return MongoClient.createShared(
        vertx,
        new JsonObject().put("connection_string", mongoUri).put("db_name", mongoDb),
        "BE_STACK_POOL");
  }

  @Override
  public void start(Promise<Void> startPromise) {

    // ************ router ***********************
    var mongoClient = initMongoClient();
    var authFilterHandler = new AuthFilterHandler();
    var userHandler = new UserHandler(mongoClient);
    var failureHandle = new FailureHandle();
    var router = Router.router(vertx);
    router
        .get("/api/user")
        .handler(authFilterHandler::handle)
        .handler(userHandler::findAllUserInfoByUserId)
        .failureHandler(failureHandle::handle);
    // ************ router ***********************

    vertx
        .createHttpServer()
        .requestHandler(router)
        .listen(
            port,
            http -> {
              if (http.succeeded()) {
                startPromise.complete();
                LOG.info("Server started on port %s".formatted(port));
              } else {
                Throwable error = http.cause();
                LOG.severe(error.getLocalizedMessage());
                startPromise.fail(error);
              }
            });
  }
}
