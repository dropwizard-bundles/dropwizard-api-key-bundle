package io.dropwizard.bundles.apikey;

import java.security.Principal;

/**
 * An interface for classes which create principal objects.
 *
 * @param <P> the type of principal
 */
@FunctionalInterface
public interface PrincipalFactory<P extends Principal> {
  /**
   * Create an instance of P from the specified name.
   */
  P create(String name);
}