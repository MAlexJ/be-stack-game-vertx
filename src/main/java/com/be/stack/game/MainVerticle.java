package com.be.stack.game;

import com.be.stack.game.handler.AuthFilterHandler;
import com.be.stack.game.handler.FailureHandle;
import com.be.stack.game.handler.UserHandler;

import io.github.cdimascio.dotenv.Dotenv;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.ext.web.Router;
import java.util.Optional;
import java.util.logging.Logger;

public class MainVerticle extends AbstractVerticle {

  private static final Logger LOG = Logger.getLogger(MainVerticle.class.getName());

  public static final String ROUTE_USER_PATH = "/api/user";

  private final Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();

  private final int port = Optional.ofNullable(dotenv.get("PORT")).map(Integer::parseInt).orElse(8080);

  @Override
  public void start(Promise<Void> startPromise) {
    initMongoClient().onSuccess(mongoClient -> {
      Router router = initRouter(mongoClient);
      vertx.createHttpServer().requestHandler(router).listen(port).onSuccess(server -> {
        LOG.info("Server started on port " + port);
        startPromise.complete();
      }).onFailure(error -> {
        LOG.severe("Failed to start server: " + error.getMessage());
        startPromise.fail(error);
      });
    }).onFailure(startPromise::fail);
  }

  private Router initRouter(MongoClient mongoClient) {
    var authFilterHandler = new AuthFilterHandler();
    var userHandler = new UserHandler(mongoClient);
    var failureHandler = new FailureHandle();
    var router = Router.router(vertx);

    router.get(ROUTE_USER_PATH).handler(authFilterHandler::handle).handler(userHandler::findAllUserInfoByUserId)
      .failureHandler(failureHandler::handle);

    return router;
  }

  private Future<MongoClient> initMongoClient() {
    return Future.future(promise -> {
      try {
        String mongoUri = dotenv.get("MONGO_URI", "mongodb://localhost:27017");
        String mongoDb = dotenv.get("MONGO_DB", "mongodb");

        var config = new JsonObject()  //
          .put("connection_string", mongoUri) //
          .put("db_name", mongoDb) //
          .put("minPoolSize", 10) //
          .put("maxPoolSize", 100) //
          .put("maxIdleTimeMS", 30000) //
          .put("waitQueueTimeoutMS", 5000) //
          .put("socketTimeoutMS", 10000) //
          .put("connectTimeoutMS", 10000) //
          .put("serverSelectionTimeoutMS", 10000);

        MongoClient client = MongoClient.createShared(vertx, config, "BE_STACK_POOL");
        LOG.info("MongoDB client initialized successfully");
        promise.complete(client);
      } catch (Exception e) {
        LOG.severe("Failed to initialize MongoDB client: " + e.getMessage());
        promise.fail(e);
      }
    });
  }
}
