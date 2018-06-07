package io.dropwizard.bundles.apikey;

import io.dropwizard.auth.PrincipalImpl;
import java.security.Principal;

/**
 * A PrincipalFactory that provides a simple implementation of a Principal provided
 * by the Dropwizard Auth module.
 */
public class DefaultPrincipalFactory implements PrincipalFactory<Principal> {
  @Override
  public Principal create(String name) {
    return new PrincipalImpl(name);
  }
}