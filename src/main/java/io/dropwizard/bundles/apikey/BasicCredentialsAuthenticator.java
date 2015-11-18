package io.dropwizard.bundles.apikey;

import com.google.common.base.Optional;
import io.dropwizard.auth.AuthenticationException;
import io.dropwizard.auth.Authenticator;
import io.dropwizard.auth.basic.BasicCredentials;
import java.security.Principal;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * An Authenticator that converts HTTP basic authentication credentials into an API key.
 */
public class BasicCredentialsAuthenticator<P extends Principal>
    implements Authenticator<BasicCredentials, P> {
  private final ApiKeyProvider provider;
  private final PrincipalFactory<P> factory;

  BasicCredentialsAuthenticator(ApiKeyProvider provider, PrincipalFactory<P> factory) {
    this.provider = checkNotNull(provider);
    this.factory = checkNotNull(factory);
  }

  @Override
  public Optional<P> authenticate(BasicCredentials credentials)
      throws AuthenticationException {
    checkNotNull(credentials);

    String username = credentials.getUsername();
    String secret = credentials.getPassword();

    ApiKey key = provider.get(username);
    if (key == null) {
      return Optional.absent();
    }

    if (!secret.equals(key.getSecret())) {
      return Optional.absent();
    }

    return Optional.of(factory.create(key.getUsername()));
  }
}
