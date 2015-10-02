package io.dropwizard.bundles.apikey;

public interface ApiKeyProvider {
  /**
   * Provide the API key for the specified user.
   */
  ApiKey get(String username);
}
