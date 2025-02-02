package com.be.stack.game.handler;

import com.be.stack.game.exception.AuthTokenException;
import io.vertx.ext.web.RoutingContext;

public class FailureHandle {

  public void handle(RoutingContext ctx) {
    var t = ctx.failure();
    if (t instanceof AuthTokenException) {
      ctx.response().setStatusCode(401).end(t.getMessage());
      return;
    }

    ctx.response().setStatusCode(400).end(t.getMessage());
  }
}
