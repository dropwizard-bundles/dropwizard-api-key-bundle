package io.dropwizard.bundles.apikey;

import io.dropwizard.auth.AuthenticationException;
import io.dropwizard.auth.basic.BasicCredentials;
import java.security.Principal;
import java.util.Optional;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class BasicCredentialsAuthenticatorTest {
  private final ApiKeyProvider provider = mock(ApiKeyProvider.class);
  private final BasicCredentialsAuthenticator<Principal> auth =
      new BasicCredentialsAuthenticator<>(provider, new DefaultPrincipalFactory());

  @Test(expected = NullPointerException.class)
  public void testNullProvider() {
    new BasicCredentialsAuthenticator<>(null, new DefaultPrincipalFactory());
  }

  @Test(expected = NullPointerException.class)
  public void testNullFactory() {
    new BasicCredentialsAuthenticator<>(provider, null);
  }

  @Test
  public void testValidCredentials() throws AuthenticationException {
    ApiKey key = newKey("username", "secret");
    when(provider.get("username")).thenReturn(key);

    BasicCredentials credentials = new BasicCredentials("username", "secret");
    Optional<Principal> actual = auth.authenticate(credentials);
    assertTrue(actual.isPresent());
    assertEquals("username", actual.get().getName());
  }

  @Test
  public void testInvalidCredentials() throws AuthenticationException {
    ApiKey key = newKey("username", "not-a-secret");
    when(provider.get("username")).thenReturn(key);

    BasicCredentials credentials = new BasicCredentials("username", "secret");
    Optional<Principal> actual = auth.authenticate(credentials);
    assertFalse(actual.isPresent());
  }

  @Test
  public void testNullCredentials() throws AuthenticationException {
    when(provider.get("username")).thenReturn(null);

    BasicCredentials credentials = new BasicCredentials("username", "secret");
    Optional<Principal> actual = auth.authenticate(credentials);
    assertFalse(actual.isPresent());
  }

  private ApiKey newKey(String username, String secret) {
    ApiKey key = mock(ApiKey.class);
    when(key.getUsername()).thenReturn(username);
    when(key.getSecret()).thenReturn(secret);

    return key;
  }
}