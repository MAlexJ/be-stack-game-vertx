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

  public static final String ROUTE_USER_PATH = "/api/user";

  private final Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();

  private final Integer port = Optional.of(dotenv.get("PORT", "8080")).map(Integer::parseInt)
    .orElseThrow(() -> new RuntimeException("Server port not set"));


  @Override
  public void start(Promise<Void> startPromise) {
    vertx.createHttpServer().requestHandler(initRouter()).listen(port, http -> {
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


  private Router initRouter() {
    var mongoClient = initMongoClient();
    var authFilterHandler = new AuthFilterHandler();
    var userHandler = new UserHandler(mongoClient);
    var failureHandle = new FailureHandle();
    var router = Router.router(vertx);

    router.get(ROUTE_USER_PATH).handler(authFilterHandler::handle).handler(userHandler::findAllUserInfoByUserId)
      .failureHandler(failureHandle::handle);

    return router;
  }

  private MongoClient initMongoClient() {
    String mongoUri = dotenv.get("MONGO_URI", "mongodb://localhost:27017");
    String mongoDb = dotenv.get("MONGO_DB", "mongodb");
    return MongoClient.createShared(vertx, new JsonObject() //
      .put("connection_string", mongoUri) //
      .put("db_name", mongoDb) //
      .put("minPoolSize", 10)  // Минимальный размер пула
      .put("maxPoolSize", 100) // Максимальный размер пула (зависит от тарифного плана Atlas)
      .put("maxIdleTimeMS", 30000)  // Закрывать неиспользуемые соединения (30 сек)
      .put("waitQueueTimeoutMS", 5000)  // Тайм-аут ожидания соединения (5 сек)
      .put("socketTimeoutMS", 10000)  // Тайм-аут для сокета (10 сек)
      .put("connectTimeoutMS", 10000) // Тайм-аут подключения (10 сек)
      .put("serverSelectionTimeoutMS", 10000), "BE_STACK_POOL");
  }
}
