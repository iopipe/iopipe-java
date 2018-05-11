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

```xml
<dependency>
  <groupId>com.iopipe</groupId>
  <artifactId>iopipe</artifactId>
  <version>1.2.0</version>
</dependency>
```

For debugging on Amazon AWS, the additional dependency is required:

```xml
<dependency>
  <groupId>com.amazonaws</groupId>
  <artifactId>aws-lambda-java-log4j2</artifactId>
  <version>1.0.0</version>
</dependency>
```

The shade plugin must also have the following transformer:

```xml
<configuration>
  <transformers>
    <transformer implementation="com.github.edwgiz.mavenShadePlugin.log4j2CacheTransformer.PluginsCacheFileTransformer" />
  </transformers>
</configuration>
```

It is highly recommended to configure the shade plugin so that it merges service resources together, this will be _especially_ important if you plan to
use a number of plugins which may exist across different packages. By default
the shade plugin will not merge resources for you and as a result plugins will
appear to disappear. As such, add the following transformer to the shade
plugin:

```xml
<configuration>
  <transformers>
    <transformer implementation="org.apache.maven.plugins.shade.resource.ServicesResourceTransformer" />
  </transformers>
</configuration>
```

To create a package which is ready for deployment you may run:

```bash
mvn package
```

If you wish to strip all debugging information in the JAR file __including__
__potentially meaningful source lines to stack traces__ you can run the
following command:

```bash
pack200 -r -G file.jar
```

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

Add the following import statement:

```java
import com.iopipe.IOpipeExecution;
import com.iopipe.SimpleRequestHandlerWrapper;
```

Add a class which extends:

```java
SimpleRequestHandlerWrapper<I, O>
```

Implement the following method:

```java
protected O wrappedHandleRequest(IOpipeExecution __exec, I __input)
```

### Implement `com.iopipe.SimpleRequestStreamHandlerWrapper`.

This class provides an implementation of `RequestStreamHandler`.

Add the following import statements:

```java
import com.amazonaws.services.lambda.runtime.Context;
import com.iopipe.IOpipeExecution;
import com.iopipe.SimpleRequestStreamHandlerWrapper;
import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;
```

Add a class which extends:

```java
SimpleRequestStreamHandlerWrapper
```

Implement the following method:

```java
protected void wrappedHandleRequest(IOpipeExecution __exec, InputStream __in, OutputStream __out) throws IOException
```

### Using the service directly.

This may be used with any request handler such as `RequestHandler` or
`RequestStreamHandler`, although it is not limited to those interfaces.

Add the following import statements:

```java
import com.amazonaws.services.lambda.runtime.Context;
import com.iopipe.IOpipeService;
```

Obtain an instance of `IOpipeService`:

```java
IOpipeService service = IOpipeService.instance();
```

Run by passing a lambda or a class which implements the functional interface
`Function<IOpipeExecution, R>`, an input object may be specified which is
usable by plugins that require it:

```java
service.<String>run(context, (exec) -> "Hello World!");
service.<String>run(context, (exec) -> "Hello World!", input);
```

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
 * `com.iopipe.plugin.<name>` or `IOPIPE_<NAME>_ENABLED`
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

```java
customMetric(String name, String value)
customMetric(String name, long value)
```

Calling either of these will add a custom metric with the specified name and
the given value. Custom metric names are limited to 128 characters.

## Event Info

This plugin records input event types and includes in the report the origin
of certain events.

It operates on the given input classes:

 * `com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent`
 * `com.amazonaws.services.lambda.runtime.events.CloudFrontEvent`
 * `com.amazonaws.services.lambda.runtime.events.KinesisEvent`
 * `com.amazonaws.services.lambda.runtime.events.KinesisFirehoseEvent`
 * `com.amazonaws.services.lambda.runtime.events.S3Event`
 * `com.amazonaws.services.lambda.runtime.events.ScheduledEvent`
 * `com.amazonaws.services.lambda.runtime.events.SNSEvent`
 * `com.amazonaws.services.s3.event.S3EventNotification`

By default this plugin is enabled and requires no changes to your code unless
you are using IOpipe via the manual method. To disable the plugin you may set
the environment variable `IOPIPE_EVENT_INFO_ENABLED` to `false`.

If you are manually using IOpipe via the `IOpipeService` instance then you will
need to pass the input object for the plugin to see that object:

```java
service.<String>run(context, (exec) -> "Hello World!", input);
```

## Labels

Labels allow you to add tags to invocations at run-time as needed. They can be
added by calling the following method in the `IOpipeExecution` instance:

```java
label(String name)
```

Label names are limited to 128 characters.

## Profiling

Information and usage on the profiler is contained within the following
document:

 * [PROFILER.md](PROFILER.md)

## Tracing

The tracing plugin is enabled by default and allows one to measure the
performance of operations within a single execution of a method. Since the
trace plugin will often be used, there are utility methods to make using it
very simple.

Import the following classes:

```java
import com.iopipe.plugin.trace.TraceMeasurement;
import com.iopipe.plugin.trace.TraceUtils;
```

Marks and measurements can be made by calling:

```java
TraceUtils.measure(IOpipeExecution execution, String __name)
```

`TraceMeasurement` can be used with try-with-resources like the following:

```java
try (TraceMeasurement m = TraceUtils.measurement(execution, "watchthis"))
{
    // Perform a lengthy operation
}
```

or it may be used without try-with-resources and manually closed.

If the plugin is not enabled then the measurement will not record anything.

Disabling the plugin can be done as followed:

 * Setting the system property `com.iopipe.plugin.trace` to `false`.
 * Setting the environment variable `IOPIPE_TRACE_ENABLED` to `false`.

# Building and Installing the Project Locally

This project requires at least Java 8 to run and additionally required Maven
to build.

Compile the project:

```bash
mvn compile
```

Compile JAR package:

```bash
mvn package
```

Run tests:

```bash
mvn test
```

Clean build:

```bash
mvn clean
```

Install the project into your own Maven repository:

```bash
mvn install
```

Generate Maven informational pages:

```bash
mvn site
```

generate JavaDoc:

```bash
mvn javadoc:javadoc
```

## License

Apache 2.0
