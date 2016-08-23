package io.dropwizard.bundles.apikey;

import com.google.common.collect.ImmutableSetMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.SetMultimap;
import io.dropwizard.auth.Authorizer;
import java.security.Principal;

/**
 * Authorizes principals based upon a mapping between {@link Principal#getName()}
 * and enabled roles.
 */
public class RoleAuthorizer<P extends Principal> implements Authorizer<P> {

  private final SetMultimap<String, String> roles;

  public RoleAuthorizer(Multimap<String, String> roles) {
    this.roles = ImmutableSetMultimap.copyOf(roles);
  }

  @Override
  public boolean authorize(P principal, String role) {
    return roles.containsEntry(principal.getName(), role);
  }
}
