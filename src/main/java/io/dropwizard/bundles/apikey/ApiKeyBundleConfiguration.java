package io.dropwizard.bundles.apikey;

public interface ApiKeyBundleConfiguration {
  /**
   * Get the configuration for how API keys should be handled by the bundle.
   */
  ApiKeyConfiguration getApiKeyConfiguration();

  AuthorizationConfiguration getAuthorizationConfiguration();
}
