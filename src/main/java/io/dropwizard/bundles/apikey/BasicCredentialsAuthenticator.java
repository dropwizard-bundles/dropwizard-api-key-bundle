package io.dropwizard.bundles.apikey;

import com.google.common.base.Optional;
import io.dropwizard.auth.AuthenticationException;
import io.dropwizard.auth.Authenticator;
import io.dropwizard.auth.basic.BasicCredentials;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * An Authenticator that converts HTTP basic authentication credentials into an API key.
 */
public class BasicCredentialsAuthenticator implements Authenticator<BasicCredentials, String> {
  private final ApiKeyProvider provider;

  BasicCredentialsAuthenticator(ApiKeyProvider provider) {
    this.provider = checkNotNull(provider);
  }

  @Override
  public Optional<String> authenticate(BasicCredentials credentials)
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

    return Optional.of(key.getUsername());
  }
}
