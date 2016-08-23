package io.dropwizard.bundles.apikey;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Optional;

public class ApiKeyConfiguration {
  private final AuthConfiguration basicConfiguration;

  @JsonCreator
  ApiKeyConfiguration(@JsonProperty("basic-http") AuthConfiguration basicConfiguration) {
    this.basicConfiguration = basicConfiguration;
  }

  /**
   * The API key configuration for HTTP basic authentication.  Can be missing.
   */
  @JsonProperty("basic-http")
  public Optional<AuthConfiguration> getBasicConfiguration() {
    return Optional.ofNullable(basicConfiguration);
  }
}
