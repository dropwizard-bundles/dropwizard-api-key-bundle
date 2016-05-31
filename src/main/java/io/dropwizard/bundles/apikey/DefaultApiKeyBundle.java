package io.dropwizard.bundles.apikey;

import io.dropwizard.auth.PermitAllAuthorizer;
import java.security.Principal;

/**
 * The DefaultApiKeyBundle class provides the base implementation of the API key-based
 * authentication, providing a simple Principal implementation and no authorization logic (permit
 * all).
 */
public class DefaultApiKeyBundle<T extends ApiKeyBundleConfiguration>
    extends ApiKeyBundle<T, Principal> {
  public DefaultApiKeyBundle() {
    super(Principal.class, new DefaultPrincipalFactory(), new PermitAllAuthorizer<>());
  }
}
