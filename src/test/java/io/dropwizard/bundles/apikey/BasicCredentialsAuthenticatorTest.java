package io.dropwizard.bundles.apikey;

import com.google.common.base.Optional;
import io.dropwizard.auth.AuthenticationException;
import io.dropwizard.auth.basic.BasicCredentials;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class BasicCredentialsAuthenticatorTest {
  private final ApiKeyProvider provider = mock(ApiKeyProvider.class);
  private final BasicCredentialsAuthenticator auth = new BasicCredentialsAuthenticator(provider);

  @Test(expected = NullPointerException.class)
  public void testNullProvider() {
    new BasicCredentialsAuthenticator(null);
  }

  @Test
  public void testValidCredentials() throws AuthenticationException {
    ApiKey expected = mock(ApiKey.class);
    when(expected.getSecret()).thenReturn("secret");
    when(provider.get("username")).thenReturn(expected);

    BasicCredentials credentials = new BasicCredentials("username", "secret");
    Optional<ApiKey> actual = auth.authenticate(credentials);
    assertTrue(actual.isPresent());
    assertEquals(expected, actual.get());
  }

  @Test
  public void testInvalidCredentials() throws AuthenticationException {
    ApiKey expected = mock(ApiKey.class);
    when(expected.getSecret()).thenReturn("not-a-secret");
    when(provider.get("username")).thenReturn(expected);

    BasicCredentials credentials = new BasicCredentials("username", "secret");
    Optional<ApiKey> actual = auth.authenticate(credentials);
    assertFalse(actual.isPresent());
  }

  @Test
  public void testNullCredentials() throws AuthenticationException {
    when(provider.get("username")).thenReturn(null);

    BasicCredentials credentials = new BasicCredentials("username", "secret");
    Optional<ApiKey> actual = auth.authenticate(credentials);
    assertFalse(actual.isPresent());
  }
}
