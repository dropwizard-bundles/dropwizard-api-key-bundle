package io.dropwizard.bundles.apikey;

import io.dropwizard.auth.AuthenticationException;
import io.dropwizard.auth.Authenticator;
import io.dropwizard.auth.basic.BasicCredentials;
import java.security.Principal;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * An Authenticator that converts HTTP basic authentication credentials into an API key.
 */
public class BasicCredentialsAuthenticator<P extends Principal>
    implements Authenticator<BasicCredentials, P> {
  private final ApiKeyProvider provider;
  private final PrincipalFactory<P> factory;

  public BasicCredentialsAuthenticator(ApiKeyProvider provider, PrincipalFactory<P> factory) {
    this.provider = checkNotNull(provider);
    this.factory = checkNotNull(factory);
  }

  @Override
  public Optional<P> authenticate(BasicCredentials credentials) throws AuthenticationException {
    checkNotNull(credentials);

    final String username = credentials.getUsername();
    final String secret = credentials.getPassword();

    return Optional.ofNullable(provider.get(username))
        .filter(k -> secret.equals(k.getSecret()))
        .map(k -> factory.create(k.getUsername()));
  }
}