package io.dropwizard.bundles.apikey;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableSetMultimap;
import com.google.common.collect.Multimap;

/**
 * Configuration for mapping between roles and API keys.
 */
public class AuthorizationConfiguration {

  private final Multimap<String, String> roles;

  public AuthorizationConfiguration(@JsonProperty("roles") Multimap<String, String> roles) {
    this.roles = roles != null ? ImmutableSetMultimap.copyOf(roles) : ImmutableSetMultimap.of();
  }

  @JsonProperty("roles")
  public Multimap<String, String> getRoles() {
    return roles;
  }
}
