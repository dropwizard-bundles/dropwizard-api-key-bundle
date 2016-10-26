package io.dropwizard.bundles.apikey;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Optional;

public class ApiKeyConfiguration {
  private final String cacheSpec;
  private final AuthConfiguration basicConfiguration;

  @JsonCreator
  ApiKeyConfiguration(
      @JsonProperty("cache-spec") String cacheSpec,
      @JsonProperty("basic-http") AuthConfiguration basicConfiguration) {
    this.cacheSpec = cacheSpec;
    this.basicConfiguration = basicConfiguration;
  }

  /**
   * The configuration for how API keys should be cached.  Can be missing.
   */
  @JsonProperty("cache-spec")
  public Optional<String> getCacheSpec() {
    return Optional.ofNullable(cacheSpec);
  }

  /**
   * The API key configuration for HTTP basic authentication.  Can be missing.
   */
  @JsonProperty("basic-http")
  public Optional<AuthConfiguration> getBasicConfiguration() {
    return Optional.ofNullable(basicConfiguration);
  }
}
