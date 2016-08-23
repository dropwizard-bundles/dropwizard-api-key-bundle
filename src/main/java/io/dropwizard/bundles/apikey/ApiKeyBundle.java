package io.dropwizard.bundles.apikey;

import java.security.Principal;

/**
 * The ApiKeyBundle class provides the base implementation of the API key-based
 * authentication, providing a simple Principal implementation and no authorization logic (permit
 * all).
 */
public class ApiKeyBundle<T extends ApiKeyBundleConfiguration>
    extends PrincipalApiKeyBundle<T, Principal> {
  public ApiKeyBundle() {
    super(Principal.class, new DefaultPrincipalFactory());
  }
}