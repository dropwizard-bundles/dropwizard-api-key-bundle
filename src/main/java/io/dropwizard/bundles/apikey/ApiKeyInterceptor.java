package io.dropwizard.bundles.apikey;

import static com.google.common.base.Preconditions.checkNotNull;
import retrofit.RequestInterceptor;

import java.util.Base64;

public final class ApiKeyInterceptor implements RequestInterceptor {
  private final String authorization;

  ApiKeyInterceptor(String user, String secret) {
    checkNotNull(user);
    checkNotNull(secret);

    String token = Base64.getEncoder().encodeToString(String.format("%s:%s", user, secret)
            .getBytes());
    authorization = "Basic " + token;
  }

  @Override
  public void intercept(RequestFacade request) {
    request.addHeader("Authorization", authorization);
  }
}