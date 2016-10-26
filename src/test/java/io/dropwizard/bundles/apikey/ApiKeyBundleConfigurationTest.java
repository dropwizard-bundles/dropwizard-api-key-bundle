package io.dropwizard.bundles.apikey;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.google.common.collect.Multimap;
import com.google.common.io.Resources;
import io.dropwizard.jackson.Jackson;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 *
 */
public class ApiKeyBundleConfigurationTest {

  @Test
  public void testGetRolesWHenRolesSpecified() throws Exception {
    final String yaml = Resources.toString(Resources.getResource("config-with-roles.yaml"),
        StandardCharsets.UTF_8);
    final TestApplicationConfiguration config = Jackson.newObjectMapper(new YAMLFactory()).readValue(yaml,
        TestApplicationConfiguration.class);

    final AuthorizationConfiguration authzOpt = config.getAuthorizationConfiguration();
    final Multimap<String, String> roles = authzOpt.getRoles();
    assertEquals(2, roles.size());

    final Collection<String> clientRoles = roles.get("client");
    assertTrue(clientRoles.contains("admin"));
    assertTrue(clientRoles.contains("user"));
  }

  public static class TestApplicationConfiguration implements ApiKeyBundleConfiguration {
    @Valid
    @NotNull
    @JsonProperty("authentication")
    private final ApiKeyConfiguration apiKeyConfiguration = null;

    @Valid
    @NotNull
    @JsonProperty("authorization")
    private final AuthorizationConfiguration authorizationConfiguration = null;

    @Override
    public ApiKeyConfiguration getApiKeyConfiguration() {
      return apiKeyConfiguration;
    }

    @Override
    public AuthorizationConfiguration getAuthorizationConfiguration() {
      return authorizationConfiguration;
    }
  }
}