package io.dropwizard.bundles.apikey;

import com.codahale.metrics.MetricRegistry;
import com.google.common.base.Optional;
import com.google.common.cache.CacheBuilderSpec;
import io.dropwizard.ConfiguredBundle;
import io.dropwizard.auth.AuthFactory;
import io.dropwizard.auth.Authenticator;
import io.dropwizard.auth.CachingAuthenticator;
import io.dropwizard.auth.basic.BasicAuthFactory;
import io.dropwizard.auth.basic.BasicCredentials;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import java.util.Map;

import static com.google.common.base.Preconditions.checkState;

/**
 * An API key bundle that allows you to configure a set of users/applications that are allowed to
 * access APIs of the application in the Dropwizard configuration file.
 */
@SuppressWarnings("UnusedDeclaration")
public class ApiKeyBundle<T extends ApiKeyBundleConfiguration> implements ConfiguredBundle<T> {
  @Override
  public void initialize(Bootstrap<?> bootstrap) {
  }

  @Override
  public void run(T bundleConfiguration, Environment environment) throws Exception {
    ApiKeyConfiguration configuration = bundleConfiguration.getApiKeyConfiguration();

    Optional<AuthConfiguration> basic = configuration.getBasicConfiguration();
    checkState(basic.isPresent(), "A basic-http configuration option must be specified");

    AuthFactory<?, String> factory = createBasicAuthFactory(basic.get(), environment.metrics());
    environment.jersey().register(AuthFactory.binder(factory));
  }

  private BasicAuthFactory<String> createBasicAuthFactory(AuthConfiguration config,
                                                          MetricRegistry metrics) {
    Authenticator<BasicCredentials, String> authenticator = createAuthenticator(config);

    Optional<String> cacheSpec = config.getCacheSpec();
    if (cacheSpec.isPresent()) {
      CacheBuilderSpec spec = CacheBuilderSpec.parse(cacheSpec.get());
      authenticator = new CachingAuthenticator<>(metrics, authenticator, spec);
    }

    return new BasicAuthFactory<>(authenticator, config.getRealm(), String.class);
  }

  private Authenticator<BasicCredentials, String> createAuthenticator(AuthConfiguration config) {
    Map<String, ApiKey> keys = config.getApiKeys();
    return new BasicCredentialsAuthenticator(keys::get);
  }
}
