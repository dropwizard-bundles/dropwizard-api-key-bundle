package io.dropwizard.bundles.apikey;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Optional;
import com.google.common.collect.Maps;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

public class AuthConfiguration {
  private final String cacheSpec;
  private final String realm;
  private final Map<String, ApiKey> keys;

  @JsonCreator
  AuthConfiguration(@JsonProperty("cache-spec") String cacheSpec,
                    @JsonProperty("realm") String realm,
                    @JsonProperty("keys") Map<String, String> keys) {
    checkNotNull(cacheSpec);
    checkNotNull(realm);
    checkNotNull(keys);

    this.cacheSpec = cacheSpec;
    this.realm = realm;
    this.keys = Maps.transformEntries(keys, new Maps.EntryTransformer<String, String, ApiKey>() {
      @Override
      public ApiKey transformEntry(String key, String value) {
        return new ApiKey(key, value);
      }
    });
  }

  /**
   * The configuration for how API keys should be cached.  Can be missing.
   */
  @JsonProperty("cache-spec")
  public Optional<String> getCacheSpec() {
    return Optional.fromNullable(cacheSpec);
  }

  /**
   * The realm to use.
   */
  @JsonProperty("realm")
  public String getRealm() {
    return realm;
  }

  /**
   * Return the API keys that this application should support indexed by application.
   */
  @JsonProperty("api-keys")
  public Map<String, ApiKey> getApiKeys() {
    return keys;
  }
}
