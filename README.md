IOpipe Telemetry Agent for Java
--------------------------------------------
[![Download](https://api.bintray.com/packages/iopipe/iopipe/iopipe/images/download.svg) ](https://bintray.com/iopipe/iopipe/iopipe/_latestVersion)[![Build status](https://circleci.com/gh/iopipe/iopipe-java.svg?style=shield&circle-token=b9a08049964f555f38ab316ba535369aa5fe8252
)](https://circleci.com/gh/iopipe/iopipe-java)

This project provides analytics and distributed tracing for event-driven
applications running on AWS Lambda using [IOpipe](https://www.iopipe.com).

# Installation & usage

Using the IOpipe service with your pre-existing and newly created classes is
quite simple. If you are using Maven it requires modification of your `pom.xml`
file, otherwise you may include the JAR file of the library to your project.

More information on using Java Lambdas on Amazon AWS can be obtained at:
<https://docs.aws.amazon.com/lambda/latest/dg/java-programming-model.html>.

Logging can be enabled by following the instructions at:
<https://docs.aws.amazon.com/lambda/latest/dg/java-logging.html>.

In the `pom.xml`, add the following block to your `<dependencies>`:

```
<dependency>
  <groupId>com.iopipe</groupId>
  <artifactId>iopipe</artifactId>
  <version>0.7</version>
</dependency>
```

For debugging on Amazon AWS, the additional dependency is required:

```
<dependency>
  <groupId>com.amazonaws</groupId>
  <artifactId>aws-lambda-java-log4j2</artifactId>
  <version>1.0.0</version>
</dependency>
```

The shade plugin must also have the following transformer:

```
<configuration>
  <transformers>
    <transformer implementation="com.github.edwgiz.mavenShadePlugin.log4j2CacheTransformer.PluginsCacheFileTransformer" />
  </transformers>
</configuration>
```

It is highly recommened to configure the shade plugin so that is merges
together service resources, this will be _especially_ important if you plan to
use a number of plugins which may exist across different packages. By default
the shade plugin will not merge resources for you and as a result plugins will
appear to disappear. As such, add the following transformer to the shade
plugin:


```
<configuration>
  <transformers>
    <transformer implementation="org.apache.maven.plugins.shade.resource.ServicesResourceTransformer" />
  </transformers>
</configuration>
```

To create a package which is ready for deployment you may run:

 * `mvn package`

If you wish to strip all debugging information in the JAR file __including__
__potentially meaningful source lines to stack traces__ you can run the
following command:

`pack200 -r -G file.jar`

Deployment is the same as other Java programs on the Amazon Lambda platform.

## Configuration

There are three ways to use the service:

 * If you are currently implementing `RequestHandler`,
   implement `com.iopipe.SimpleRequestHandlerWrapper`
 * If you are currently implementing `RequestStreamHandler`,
   implement `com.iopipe.SimpleRequestStreamHandlerWrapper`
 * You may also interact with IOpipe directly

### Implement `com.iopipe.SimpleRequestHandlerWrapper`.

This class provides an implementation of `RequestHandler<I, O>`.

 * Add the following import statement:
   * `import com.iopipe.IOpipeExecution;`
   * `import com.iopipe.SimpleRequestHandlerWrapper;`
 * Add a class which extends:
   * `SimpleRequestHandlerWrapper<I, O>`
 * Implement the following method:
   * `protected O wrappedHandleRequest(IOpipeExecution __exec, I __input)`

### Implement `com.iopipe.SimpleRequestStreamHandlerWrapper`.

This class provides an implementation of `RequestStreamHandler`.

 * Add the following import statements:
   * `import com.amazonaws.services.lambda.runtime.Context;`
   * `import com.iopipe.IOpipeExecution;`
   * `import com.iopipe.SimpleRequestStreamHandlerWrapper;`
   * `import java.io.InputStream;`
   * `import java.io.IOException;`
   * `import java.io.OutputStream;`
 * Add a class which extends:
   * `SimpleRequestStreamHandlerWrapper`
 * Implement the following method:
   * `protected void wrappedHandleRequest(IOpipeExecution __exec, InputStream __in, `
     `OutputStream __out) throws IOException`

### Using the service directly.

This may be used with any request handler such as `RequestHandler` or
`RequestStreamHandler`, although it is not limited to those interfaces.

 * Add the following import statements:
   * `import com.amazonaws.services.lambda.runtime.Context;`
   * `import com.iopipe.IOpipeService;`
 * Obtain an instance of `IOpipeService`:
   * `IOpipeService service = IOpipeService.instance();`
 * Run by passing a lambda or a class which implements the functional
   interface `Function<IOpipeExecution, R>`:
   * `service.<String>run(context, (exec) -> "Hello World!");`

### Setting system properties and environment variables

Set up IOpipe using system properties or environment variables. N.B., it is necessary
to use environment variables for running on AWS Lambda. System properties will
take precedence when available.

* `com.iopipe.enabled` or `IOPIPE_ENABLED`
  * If this is set and if the value is `true` (ignoring case) then the library
    will be enabled.
  * If this is set to `false`.
  * If this is not set then internally it is treated as being `true`.
* `com.iopipe.installmethod` or `IOPIPE_INSTALL_METHOD`
* `com.iopipe.timeoutwindow` or `IOPIPE_TIMEOUT_WINDOW`
  * This time is subtracted from the duration that a lambda may operate on
    the service.
  * If this is zero then the window is disabled.
  * If this is not set then it defaults to `150`.
* `com.iopipe.token`, `IOPIPE_TOKEN`
  * This represents the token of the IOpipe collector which is to obtain
    statistics.
  * This is the default token which will be used if no token was specified in
    the client.
  * If you need help looking for your token you can visit:
    [Find your project token](https://dashboard.iopipe.com/install).
 * `com.iopipe.plugin.<name>` or `IOPIPE_<NAME>_ENABLE`
   * If set to `true` then the specified plugin will be enabled.
   * If set to `false` then the plugin will be disabled.
   * If this is not set for a plugin then it will use the setting from the
     plugin if it should be enabled by default or not.

Log4j2 is used for debugging output and it can be configured via environment
variable. Information on its configuration is at:

* <https://logging.apache.org/log4j/2.x/manual/configuration.html>
* <https://docs.aws.amazon.com/lambda/latest/dg/java-logging.html>

The associated package is `com.iopipe`.

## Custom Metrics

To use custom metrics, you can simply call the following two methods in the
`IOpipeExecution` instance:

 * `customMetric(String name, String value)`
 * `customMetric(String name, long value)`

Calling either of these will add a custom metric with the specified name and
the given value.

## Tracing

The tracing plugin is enabled by default and allows one to measure the
performance of operations within a single execution of a method. Since the
trace plugin will often be used, there are utility methods to make using it
very simple.

Import the following classes:

```
import com.iopipe.plugin.trace.TraceMeasurement;
import com.iopipe.plugin.trace.TraceUtils;
```

Marks and measurements can be made by calling:

 * `TraceUtils.measure(IOpipeExecution execution, String __name)`

`TraceMeasurement` can be used with try-with-resources like the following:

```
try (TraceMeasurement m = TraceUtils.measurement(execution, "watchthis"))
{
    // Perform a lengthy operation
}
```

or it may be used without try-with-resources and manually closed.

If the plugin is not enabled then the measurement will not record anything.

Disabling the plugin can be done as followed:

 * Setting the system property `com.iopipe.plugin.trace` to `false`.
 * Setting the environment variable `IOPIPE_TRACE_ENABLE` to `false`.

# Building and Installing the Project Locally

This project requires at least Java 8 to run and additionally required Maven
to build.

* `mvn compile`         -- Compile the project.
* `mvn package`         -- Compile JAR package.
* `mvn test`            -- Run tests.
* `mvn clean`           -- Clean build.
* `mvn install`         -- Install the project into your own Maven repository.
* `mvn site`            -- Generate Maven informational pages.
* `mvn javadoc:javadoc` -- Generate JavaDoc.
