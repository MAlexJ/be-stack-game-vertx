package com.be.stack.game.handler;

import com.be.stack.game.exception.AuthTokenException;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.ext.web.RoutingContext;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AuthFilterHandler {

  private static final Logger LOG = Logger.getLogger(AuthFilterHandler.class.getName());

  public void handle(RoutingContext context) {
    HttpServerRequest request = context.request();
    var userId =
        Optional.ofNullable(request.getHeader("X-Auth-Token"))
            .map(val -> URLDecoder.decode(val, StandardCharsets.UTF_8))
            .map(
                decodedVal -> {
                  try {
                    return decodedVal.substring(
                        decodedVal.indexOf("\"id\"") + 5, decodedVal.indexOf(",\"first"));
                  } catch (Exception e) {
                    throw new AuthTokenException("Invalid `tgInitData` data");
                  }
                })
            .orElseThrow(() -> new AuthTokenException("X-Auth-Token not present"));

    if (LOG.isLoggable(Level.INFO)) {
      LOG.info("X-Auth-Token: user id - %s".formatted(userId));
    }

    context.put("userId", userId);
    context.next();
  }
}
