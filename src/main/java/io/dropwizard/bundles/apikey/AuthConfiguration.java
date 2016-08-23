package io.dropwizard.bundles.apikey;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSetMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.SetMultimap;
import java.util.Map;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkNotNull;

public class AuthConfiguration {
  private final String cacheSpec;
  private final String realm;
  private final Map<String, ApiKey> keys;
  private final Multimap<String, String> roles;

  @JsonCreator
  AuthConfiguration(@JsonProperty("cache-spec") String cacheSpec,
                    @JsonProperty("realm") String realm,
                    @JsonProperty("keys") Map<String, String> keys,
                    @JsonProperty("roles") SetMultimap<String, String> roles) {
    checkNotNull(cacheSpec);
    checkNotNull(realm);
    checkNotNull(keys);

    this.cacheSpec = cacheSpec;
    this.realm = realm;
    this.keys = ImmutableMap.copyOf(Maps.transformEntries(keys, ApiKey::new));
    this.roles = roles != null ? ImmutableSetMultimap.copyOf(roles) : ImmutableSetMultimap.of();
  }

  /**
   * The configuration for how API keys should be cached.  Can be missing.
   */
  @JsonProperty("cache-spec")
  public Optional<String> getCacheSpec() {
    return Optional.ofNullable(cacheSpec);
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

  @JsonProperty("roles")
  public Multimap<String, String> getRoles() {
    return roles;
  }
}
