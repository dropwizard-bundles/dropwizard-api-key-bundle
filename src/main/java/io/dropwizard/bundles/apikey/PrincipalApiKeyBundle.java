package io.dropwizard.bundles.apikey;

import com.codahale.metrics.MetricRegistry;
import com.google.common.cache.CacheBuilderSpec;
import io.dropwizard.ConfiguredBundle;
import io.dropwizard.auth.AuthDynamicFeature;
import io.dropwizard.auth.AuthValueFactoryProvider;
import io.dropwizard.auth.Authenticator;
import io.dropwizard.auth.Authorizer;
import io.dropwizard.auth.CachingAuthenticator;
import io.dropwizard.auth.DefaultUnauthorizedHandler;
import io.dropwizard.auth.PermitAllAuthorizer;
import io.dropwizard.auth.UnauthorizedHandler;
import io.dropwizard.auth.basic.BasicCredentialAuthFilter;
import io.dropwizard.auth.basic.BasicCredentials;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import java.security.Principal;
import java.util.Map;
import java.util.Optional;
import javax.annotation.Nullable;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

/**
 * An API key bundle that allows you to configure a set of users/applications that are allowed to
 * access APIs of the application in the Dropwizard configuration file. The API key bundle is bound
 * to an ApiKeyBundleConfiguration type and a Principal type. You can use the ApiKeyBundle
 * class if you use use a default Principal implementation and do not require authorization.
 */
public class PrincipalApiKeyBundle<T extends ApiKeyBundleConfiguration, P extends Principal>
    implements ConfiguredBundle<T> {
  private final Class<P> principalClass;
  private final PrincipalFactory<P> factory;
  private final Authorizer<P> authorizer;
  private final UnauthorizedHandler unauthorizedHandler;

  /**
   * Construct the PrincipalApiKeyBundle using the provided Principal class, PrincipalFactory and
   * Authorizer.
   *
   * @param principalClass The class of the class extending the Principal type.
   * @param factory        The PrincipalFactory instance, which can create new P objects.
   */
  public PrincipalApiKeyBundle(Class<P> principalClass, PrincipalFactory<P> factory) {
    this(principalClass, factory, null, new DefaultUnauthorizedHandler());
  }

  /**
   * Construct the PrincipalApiKeyBundle using the provided Principal class, PrincipalFactory,
   * Authorizer and UnauthorizedHandler.
   *
   * @param principalClass      The class of the class extending the Principal type.
   * @param factory             The PrincipalFactory instance, which can create new P objects.
   * @param unauthorizedHandler The UnauthorizedHandler instance.
   */
  public PrincipalApiKeyBundle(Class<P> principalClass, PrincipalFactory<P> factory,
                               @Nullable Authorizer<P> authorizer,
                               UnauthorizedHandler unauthorizedHandler) {
    this.principalClass = checkNotNull(principalClass);
    this.factory = checkNotNull(factory);
    this.authorizer = authorizer;
    this.unauthorizedHandler = checkNotNull(unauthorizedHandler);
  }

  @Override
  public void initialize(Bootstrap<?> bootstrap) {
  }

  @Override
  public void run(T bundleConfiguration, Environment environment) throws Exception {
    ApiKeyConfiguration configuration = bundleConfiguration.getApiKeyConfiguration();

    Optional<AuthConfiguration> basic = configuration.getBasicConfiguration();
    checkState(basic.isPresent(), "A basic-http configuration option must be specified");

    environment.jersey().register(new AuthDynamicFeature(
        createBasicCredentialAuthFilter(basic.get(), environment.metrics())));
    environment.jersey().register(new AuthValueFactoryProvider.Binder<>(principalClass));
  }

  private BasicCredentialAuthFilter<P> createBasicCredentialAuthFilter(
      AuthConfiguration config,
      MetricRegistry metrics) {
    final Authorizer<P> authorizer;
    if (this.authorizer != null) {
      authorizer = this.authorizer;
    } else if (!config.getRoles().isEmpty()) {
      authorizer = new RoleAuthorizer<>(config.getRoles());
    } else {
      authorizer = new PermitAllAuthorizer<>();
    }

    return new BasicCredentialAuthFilter.Builder<P>()
        .setAuthenticator(createAuthenticator(config, metrics))
        .setRealm(config.getRealm())
        .setAuthorizer(authorizer)
        .setUnauthorizedHandler(unauthorizedHandler)
        .buildAuthFilter();
  }

  private Authenticator<BasicCredentials, P> createAuthenticator(AuthConfiguration config,
                                                                 MetricRegistry metrics) {
    Map<String, ApiKey> keys = config.getApiKeys();
    Authenticator<BasicCredentials, P> authenticator =
        new BasicCredentialsAuthenticator<>(keys::get, factory);

    final Optional<Authenticator<BasicCredentials, P>> auth = config.getCacheSpec()
        .map(s -> new CachingAuthenticator<>(metrics, authenticator, CacheBuilderSpec.parse(s)));
    return auth.orElse(authenticator);
  }
}