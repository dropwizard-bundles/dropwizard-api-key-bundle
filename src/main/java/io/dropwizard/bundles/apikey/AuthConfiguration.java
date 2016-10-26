package io.dropwizard.bundles.apikey;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSetMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.SetMultimap;
import java.util.Map;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkNotNull;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AuthConfiguration {
  private final String realm;
  private final Map<String, ApiKey> keys;

  @JsonCreator
  AuthConfiguration(
      @JsonProperty("realm") String realm,
      @JsonProperty("keys") Map<String, String> keys) {
    this.realm = checkNotNull(realm);
    this.keys = ImmutableMap.copyOf(Maps.transformEntries(checkNotNull(keys), ApiKey::new));
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
