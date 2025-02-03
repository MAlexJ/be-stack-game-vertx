package com.be.stack.game.security;

import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.handler.CorsHandler;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CorsConfigurationHandler {

  private CorsConfigurationHandler() {
  }

  public static final String ALLOWED_API_PATTERN = "/api/*";

  private static final String ALLOWED_ORIGIN = "http://localhost:3000";

  private static final String AUTHORIZATION_HEADER = "Authorization";

  private static final String X_AUTH_HEADER = "X-Auth-Header";

  /**
   * Initializes and returns a configured CorsHandler.
   *
   * @return the configured CorsHandler
   */
  public static CorsHandler initializeCorsHandler() {
    return CorsHandler.create() //
      .addRelativeOrigins(List.of(ALLOWED_ORIGIN)) //
      .allowedHeaders(getAllowedHeaders())   //
      .allowedMethods(getAllowedMethods());
  }

  /**
   * Returns the set of allowed headers for CORS requests.
   *
   * @return the set of allowed headers
   */
  private static Set<String> getAllowedHeaders() {
    Set<String> allowedHeaders = new HashSet<>();
    allowedHeaders.add(AUTHORIZATION_HEADER);
    allowedHeaders.add(X_AUTH_HEADER);
    allowedHeaders.add("Access-Control-Allow-Origin");
    allowedHeaders.add("Origin");
    allowedHeaders.add("Content-Type");
    allowedHeaders.add("Accept");
    return allowedHeaders;
  }

  /**
   * Returns the set of allowed HTTP methods for CORS requests.
   *
   * @return the set of allowed HTTP methods
   */
  private static Set<HttpMethod> getAllowedMethods() {
    Set<HttpMethod> allowedMethods = new HashSet<>();
    allowedMethods.add(HttpMethod.GET);
    allowedMethods.add(HttpMethod.POST);
    allowedMethods.add(HttpMethod.PUT);
    allowedMethods.add(HttpMethod.PATCH);
    allowedMethods.add(HttpMethod.OPTIONS);
    return allowedMethods;
  }
}
