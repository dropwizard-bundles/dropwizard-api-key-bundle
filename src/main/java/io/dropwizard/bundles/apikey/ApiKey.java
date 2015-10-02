package io.dropwizard.bundles.apikey;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * An API key that represents a caller into an application.
 */
public class ApiKey {
  private final String username;
  private final String secret;

  /**
   * Construct a new API key with the provided username and secret.
   *
   * @param username The username for the API key.
   * @param secret   The secret for the API key.
   */
  @JsonCreator
  public ApiKey(@JsonProperty("username") String username,
                @JsonProperty("password") String secret) {
    this.username = checkNotNull(username);
    this.secret = checkNotNull(secret);
  }

  /**
   * Retrieve the username associated with the API key.
   *
   * @return The username.
   */
  @JsonProperty("username")
  public String getUsername() {
    return username;
  }

  /**
   * Retrieve the secret associated with the API key.
   *
   * @return The secret.
   */
  @JsonProperty("secret")
  public String getSecret() {
    return secret;
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("username", username)
        .toString();
  }
}
