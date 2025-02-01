package com.be.stack.game;

import io.github.cdimascio.dotenv.Dotenv;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MainVerticle extends AbstractVerticle {

  private static final Logger LOG = Logger.getLogger(MainVerticle.class.getName());

  @Override
  public void start(Promise<Void> startPromise) {
    Dotenv dotenv = Dotenv.load();
    int port = Integer.parseInt(Objects.requireNonNull(dotenv.get("SERVER_PORT", "27017")));
    String mongoUri = dotenv.get("MONGO_URI", "mongodb://localhost:27017");
    String mongoDb = dotenv.get("MONGO_DB", "mongodb");

    MongoClient mongoClient =
        MongoClient.createShared(
            vertx,
            new JsonObject().put("connection_string", mongoUri).put("db_name", mongoDb),
            "BE_STACK_POOL");

    Router router = Router.router(vertx);
    UserHandler userHandler = new UserHandler(mongoClient);

    // Global request filter-like behavior
    router.route().handler(this::requestFilterHandler);

    router.get("/api/webapp/users").handler(userHandler::getUsers);
    router.get("/api/webapp/users/:userId").handler(userHandler::getUserById);
    router.post("/api/webapp/users").handler(userHandler::createUser);

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

  // Global filter-like handler
  private void requestFilterHandler(RoutingContext context) {
    HttpServerRequest request = context.request();

    if (LOG.isLoggable(Level.FINE)) {
      LOG.fine("Request received for: %s".formatted(request.uri()));
    }

    // Continue processing the request (pass to the next handler)
    context.next(); // This ensures the next handler in the chain is called
  }
}
