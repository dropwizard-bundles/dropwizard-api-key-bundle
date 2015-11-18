# dropwizard-api-key-bundle

A [Dropwizard][dropwizard] bundle that provides a simple way to manage API keys for callers of
your service. The bundle provides support for authentication only; authorization is supported
by optionally providing an `Authorizer` as documented below.

[![Build Status](https://secure.travis-ci.org/dropwizard-bundles/dropwizard-api-key-bundle.png?branch=dropwizard-0.9)]
(http://travis-ci.org/dropwizard-bundles/dropwizard-api-key-bundle)


## Getting Started

Just add this maven dependency to get started:

```xml
<dependency>
  <groupId>io.dropwizard-bundles</groupId>
  <artifactId>dropwizard-api-key-bundle</artifactId>
  <version>0.9.1-1-SNAPSHOT</version>
</dependency>
```

If you only need authentication and a default `Principal` implementation add the default
version of the bundle to your environment:

```java
public class MyApplication extends Application<MyConfiguration> {
  @Override
  public void initialize(Bootstrap<MyConfiguration> bootstrap) {
    bootstrap.addBundle(new DefaultApiKeyBundle<>());
  }

  @Override
  public void run(MyConfiguration cfg, Environment env) throws Exception {
    // ...
  }
}
```

If you need to provide an `Authorizer` or a different `Principal` (extending type), or both,
add the bundle to your environment and provide the type extending the `Principal` interface, an
implementation of the `Authorizer` and `PrincipalFactory` as appropriate:

```java
public class MyApplication extends Application<MyConfiguration> {
  @Override
  public void initialize(Bootstrap<MyConfiguration> bootstrap) {
    bootstrap.addBundle(new ApiKeyBundle<>(User.class, new PrincipalFactory<User>() {
      @Override
      public User create(String name) {
        // Do something interesting...
        return new User(name);
      }}, new Authorizer<User>() {
        @Override
        public boolean authorize(User user, String role) {
          return user.getName().equals("application-1") && role.equals("ADMIN");
        }
    });
  }

  @Override
  public void run(MyConfiguration cfg, Environment env) throws Exception {
    // ...
  }
}
```

You will also need to make your `MyConfiguration` class implement `ApiKeyBundleConfiguration` in
order to provide the bundle with the necessary information it needs to know your API keys.

```java
public class MyConfiguration implements ApiKeyBundleConfiguration {
  @Valid
  @NotNull
  @JsonProperty("authentication")
  private final ApiKeyConfiguration apiKeyConfiguration = null;

  /**
   * Return the API key configuration.
   */
  @Override
  public ApiKeyConfiguration getApiKeyConfiguration() {
    return apiKeyConfiguration;
  }
```

Now you can use API key based authentication in your application by declaring a method on a resource
that has an `@Auth` annotated `Principal` parameter (or an extending type).  See the
[Dropwizard Authentication][authentication] documentation for more details.  The name of the `Principal`
will be the name of the application that made the request if the authentication process was
successful.

As far as configuration goes you can define your API keys in your application's config file.
Assuming, like in the above example, you called your API key configuration element `authentication`
then you can use a config file like this:

```yaml
authentication:
  basic-http:
    cache-spec: maximumSize=1000, expireAfterAccess=10m
    realm: MyApplication
    keys:
      application-1: api-key-1
      application-2: api-key-2
```

[dropwizard]: http://dropwizard.io
[authentication]: http://www.dropwizard.io/0.9.1/docs/manual/auth.html