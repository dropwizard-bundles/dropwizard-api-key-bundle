# dropwizard-api-key-bundle

A [Dropwizard][dropwizard] bundle that provides a simple way to manage API keys for callers of
your service.

[![Build Status](https://secure.travis-ci.org/dropwizard-bundles/dropwizard-api-key-bundle.png?branch=master)]
(http://travis-ci.org/dropwizard-bundles/dropwizard-api-key-bundle)


## Getting Started

Just add this maven dependency to get started:

```xml
<dependency>
  <groupId>io.dropwizard-bundles</groupId>
  <artifactId>dropwizard-api-key-bundle</artifactId>
  <version>0.8.4</version>
</dependency>
```

Add the bundle to your environment using your choice of version supplier:

```java
public class MyApplication extends Application<MyConfiguration> {
  @Override
  public void initialize(Bootstrap<MyConfiguration> bootstrap) {
    bootstrap.addBundle(new ApiKeyBundle<>());
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
that has an `@Auth` annotated `ApiKey` parameter.  See the
[Dropwizard Authentication](authentication) documentation for more details.

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
[authentication]: http://www.dropwizard.io/manual/auth.html