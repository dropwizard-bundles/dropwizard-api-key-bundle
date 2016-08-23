package io.dropwizard.bundles.apikey;

import com.google.common.collect.ImmutableSetMultimap;
import io.dropwizard.auth.Auth;
import io.dropwizard.auth.AuthDynamicFeature;
import io.dropwizard.auth.AuthValueFactoryProvider;
import io.dropwizard.auth.basic.BasicCredentialAuthFilter;
import io.dropwizard.testing.junit.ResourceTestRule;
import java.security.Principal;
import java.util.Base64;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.glassfish.jersey.server.filter.RolesAllowedDynamicFeature;
import org.glassfish.jersey.test.grizzly.GrizzlyWebTestContainerFactory;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ApiKeyBundleClientTest {
  @Path("/test")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  public static final class TestResource {
    @GET
    @Path("/insecure")
    public String insecure() {
      return "insecure";
    }

    @GET
    @Path("/secure")
    public String secure(@Auth Principal application) {
      return "secure";
    }

    @GET
    @Path("/authorized")
    @RolesAllowed("admin")
    public String authorized(@Auth Principal application) {
      return "scope";
    }
  }

  private static final ApiKeyProvider provider = new ApiKeyProvider() {
    private final ApiKey KEY = new ApiKey("username", "secret");
    private final ApiKey UNAUTHORIZED = new ApiKey("unauthorized", "secret");

    @Override
    public ApiKey get(String username) {
      if ("username".equals(username)) {
        return KEY;
      } else if ("unauthorized".equals(username)) {
        return UNAUTHORIZED;
      }

      return null;
    }
  };

  private final BasicCredentialsAuthenticator<Principal> authenticator =
      new BasicCredentialsAuthenticator<>(provider, new DefaultPrincipalFactory());

  private final RoleAuthorizer<Principal> authorizer
      = new RoleAuthorizer<>(ImmutableSetMultimap.of("username", "admin"));

  private BasicCredentialAuthFilter<Principal> authFilter =
      new BasicCredentialAuthFilter.Builder<>()
          .setAuthenticator(authenticator)
          .setRealm("realm")
          .setAuthorizer(authorizer)
          .buildAuthFilter();

  @Rule
  public final ResourceTestRule resources = ResourceTestRule.builder()
      .setTestContainerFactory(new GrizzlyWebTestContainerFactory())
      .addProvider(new AuthDynamicFeature(authFilter))
      .addProvider(new AuthValueFactoryProvider.Binder<>(Principal.class))
      .addProvider(RolesAllowedDynamicFeature.class)
      .addResource(new TestResource())
      .build();

  @Test
  public void testInsecureWithNoBasicAuthHeaders() {
    Response response = resources.getJerseyTest()
        .target("/test/insecure")
        .request()
        .get();

    assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
  }

  @Test
  public void testInsecureWithValidBasicAuthHeaders() {
    Response response = resources.getJerseyTest()
        .target("/test/insecure")
        .request()
        .header(HttpHeaders.AUTHORIZATION, token("username", "secret"))
        .get();

    assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
  }

  @Test
  public void testInsecureWithInvalidBasicAuthHeaders() {
    Response response = resources.getJerseyTest()
        .target("/test/insecure")
        .request()
        .header(HttpHeaders.AUTHORIZATION, token("wrong", "wrong"))
        .get();

    // Should still succeed because this isn't an authenticated endpoint.
    assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
  }

  @Test
  public void testSecureWithNoBasicAuthHeaders() {
    Response response = resources.getJerseyTest()
        .target("/test/secure")
        .request()
        .get();

    assertEquals(Response.Status.UNAUTHORIZED.getStatusCode(), response.getStatus());
  }

  @Test
  public void testSecureWithValidBasicAuthHeaders() {
    Response response = resources.getJerseyTest()
        .target("/test/secure")
        .request()
        .header(HttpHeaders.AUTHORIZATION, token("username", "secret"))
        .get();

    assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
  }

  @Test
  public void testSecureWithInvalidBasicAuthHeaders() {
    Response response = resources.getJerseyTest()
        .target("/test/secure")
        .request()
        .header(HttpHeaders.AUTHORIZATION, token("wrong", "wrong"))
        .get();

    assertEquals(Response.Status.UNAUTHORIZED.getStatusCode(), response.getStatus());
  }

  @Test
  public void testAuthorizedWithAuthorizedBasicAuthHeaders() {
    Response response = resources.getJerseyTest()
        .target("/test/authorized")
        .request()
        .header(HttpHeaders.AUTHORIZATION, token("username", "secret"))
        .get();

    assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
  }

  @Test
  public void testAuthorizedWithUnauthorizedBasicAuthHeaders() {
    Response response = resources.getJerseyTest()
        .target("/test/authorized")
        .request()
        .header(HttpHeaders.AUTHORIZATION, token("unauthorized", "secret"))
        .get();

    assertEquals(Response.Status.FORBIDDEN.getStatusCode(), response.getStatus());
  }

  private static String token(String username, String secret) {
    byte[] credentials = (username + ":" + secret).getBytes();
    return "Basic " + Base64.getEncoder().encodeToString(credentials);
  }
}