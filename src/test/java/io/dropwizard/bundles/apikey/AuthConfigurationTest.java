package io.dropwizard.bundles.apikey;

import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.google.common.collect.Multimap;
import com.google.common.io.Resources;
import io.dropwizard.jackson.Jackson;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 *
 */
public class AuthConfigurationTest {

  @Test
  public void testGetRolesWHenRolesSpecified() throws Exception {
    final String yaml = Resources.toString(Resources.getResource("config-with-roles.yaml"),
        StandardCharsets.UTF_8);
    final AuthConfiguration config = Jackson.newObjectMapper(new YAMLFactory()).readValue(yaml,
        AuthConfiguration.class);

    final Multimap<String, String> roles = config.getRoles();
    assertEquals(2, roles.size());

    final Collection<String> clientRoles = roles.get("client");
    assertTrue(clientRoles.contains("admin"));
    assertTrue(clientRoles.contains("user"));
  }
}