IOpipe Telemetry Agent for Java
--------------------------------------------
[![Download](https://api.bintray.com/packages/iopipe/iopipe/iopipe/images/download.svg) ](https://bintray.com/iopipe/iopipe/iopipe/_latestVersion)[![Build status](https://circleci.com/gh/iopipe/iopipe-java.svg?style=shield&circle-token=b9a08049964f555f38ab316ba535369aa5fe8252
)](https://circleci.com/gh/iopipe/iopipe-java)[![Javadocs](https://www.javadoc.io/badge/com.iopipe/iopipe.svg)](https://www.javadoc.io/doc/com.iopipe/iopipe)

This project provides analytics and distributed tracing for event-driven
applications running on AWS Lambda using [IOpipe](https://www.iopipe.com).

It is licensed under the Apache 2.0.

 * [Building With IOpipe](#building-with-iopipe)
   * [Maven](#maven)
   * [Gradle](#gradle)
 * [Configuration](#configuration)
 * [Wrapping Your Lambda](#wrapping-your-lambda)
   * [Generic Entry Point Wrappers](#generic-entry-point-wrappers)
     * [`RequestHandler`](#requesthandler)
     * [`RequestStreamHandler`](#requeststreamhandler)
   * [Implement `com.iopipe.SimpleRequestHandlerWrapper`](#implement-comiopipesimplerequesthandlerwrapper)
   * [Implement `com.iopipe.SimpleRequestStreamHandlerWrapper`](#implement-comiopipesimplerequeststreamhandlerwrapper)
   * [Wrapping Without A Helper Class](#wrapping-without-a-helper-class)
 * [Accessing IOpipe's `IOpipeExecution` Instance](#accessing-iopipes-iopipeexecution-instance)
 * [Accessing the AWS `Context` Object](#accessing-the-aws-context-object)
 * [Measuring and Monitoring](#measuring-and-monitoring)
   * [Custom Metrics](#custom-metrics)
   * [Event Info](#event-info)
   * [Labels](#labels)
   * [Profiling](#profiling)
   * [Tracing](#tracing)
 * [Resources](#resources)

# Building With IOpipe

This agent is available in Maven Central and Bintray and either can be used for
including the agent in your library.

## Maven

Your `pom.xml` file may be modified to include the following dependency:

```xml
<dependency>
  <groupId>com.iopipe</groupId>
  <artifactId>iopipe</artifactId>
  <version>1.9.0</version>
</dependency>
```

It is highly recommended that you use the [Shade Plugin](https://maven.apache.org/plugins/maven-shade-plugin/index.html)
for Maven since AWS requires that all classes and files are packed into a
single JAR.

If you are using third-party IOpipe plugins or are writing your own you should
in your POM include the [service resource transformer for shading](https://maven.apache.org/plugins/maven-shade-plugin/examples/resource-transformers.html#ServicesResourceTransformer).

If your JAR file is too big you may try [reducing the size of your JAR using the shade plugin](https://maven.apache.org/plugins/maven-shade-plugin/examples/includes-excludes.html).
If that does not reduce the size of your JAR enough and you need more space
you can __strip all debugging and source line information__, __which makes
debugging much more difficult__, by running `pack200 -r -G path-to-target.jar`.

## Gradle

For a basic configuration with Gradle there is [an example build.gradle](https://github.com/iopipe/examples/blob/master/java/build.gradle) that you may use as a base for your
project.

# Wrapping your Lambda

There are four ways to wrap your lambda:

 * Using one of the generic entry point wrappers.
 * If you are currently implementing `RequestHandler`,
   extend the class `com.iopipe.SimpleRequestHandlerWrapper`.
 * If you are currently implementing `RequestStreamHandler`,
   extend the class `com.iopipe.SimpleRequestStreamHandlerWrapper`.
 * You may also initialize the IOpipe wrapper yourself.

## Generic Entry Point Wrappers

By setting the entry point of the lambda in the configuration to a specific
generic handler class then setting `IOPIPE_GENERIC_HANDLER` you may wrap
any standard AWS entry point with IOpipe without needing to modify any code.

If the exception `com.iopipe.generic.InvalidEntryPointException` or
`com.iopipe.IOpipeFatalError` is thrown the message detail should specify
mis-configuration or a handler that cannot be used.

## `RequestHandler`

Set the entry point of your lambda to:

 * `com.iopipe.generic.GenericAWSRequestHandler`.

The expected method signatures are:

 * `(T)`
 * `(T, Context)`
 * `(IOpipeExecution, T)`

## `RequestStreamHandler`

Set the entry point of your lambda to:

 * `com.iopipe.generic.GenericAWSRequestStreamHandler`

The expected method signatures are:

 * `(InputStream, OutputStream)`
 * `(InputStream, OutputStream, Context)`
 * `(IOpipeExecution, InputStream, OutputStream)`

## Implement `com.iopipe.SimpleRequestHandlerWrapper`

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

## Implement `com.iopipe.SimpleRequestStreamHandlerWrapper`

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

## Wrapping Without A Helper Class

If you are unable to wrap using the `SimpleRequestHandlerWrapper` or
`SimpleRequestStreamHandlerWrapper` you may manually wrap your method and then
execute that method or code.

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

# Configuration

IOpipe may be configured using system properties and/or environment variables.
Note that on AWS Lambda, environment variables must be used.
If you do specify system properties then they will take precedence before
environment variables.

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
* `com.iopipe.token` or `IOPIPE_TOKEN`
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
 * `com.iopipe.generichandler` or `IOPIPE_GENERIC_HANDLER`
   * This specifies the class (and optionally the method) to be used by the
     generic handler to wrap with IOpipe.
   * `com.example`, implies that the `requestHandler` method be used.
   * `com.example::requestHandler` specifies both a class and method.
 * `com.iopipe.collectorurl` or `IOPIPE_COLLECTOR_URL`
   * Alternative URL for the collector, this is mostly used for debugging and
     experimentation with newer collectors.

Alternatively a configuration may be specified in the root of the JAR with a
standard properties format (`key=value`) which is named `iopipe.properties`.
The configuration values are only used if they have not been specified by
system properties or environment variables. Generally using this is not
recommended because it would require a redeploy to change the settings. Also
the token itself is sensitive and should not be placed in the configuration.

IOpipe uses tinylog for its internal logging, to make debug output from IOpipe
easier to see tinylog can be configured using the following information located
at:

 * <https://tinylog.org/configuration>

The associated package is `com.iopipe`.

# Accessing IOpipe's `IOpipeExecution` Instance

If `IOpipeExecution` needs to be obtained then you may use:

 * `IOpipeExecution.currentExecution()`

# Accessing the AWS `Context` Object

The AWS `Context` object may be obtained by invoking `context()` on
the `IOpipeExecution` instance. For example:

```java
protected final String wrappedHandleRequest(IOpipeExecution __exec, String __n)
{
    // Code here...
    
    Context context = __exec.context();
    
    // Code here...
}
```

# Measuring and Monitoring

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
 * `com.amazonaws.services.lambda.runtime.events.SQSEvent`
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
TraceUtils.measure(String __name)
TraceUtils.measure(IOpipeExecution execution, String __name)
```

`TraceMeasurement` can be used with try-with-resources like the following:

```java
try (TraceMeasurement m = TraceUtils.measurement("watchthis"))
{
    // Perform a lengthy operation
}
```

or it may be used without try-with-resources and manually closed.

If the plugin is not enabled then the measurement will not record anything.

Disabling the plugin can be done as followed:

 * Setting the system property `com.iopipe.plugin.trace` to `false`.
 * Setting the environment variable `IOPIPE_TRACE_ENABLED` to `false`.

# Resources

For this agent:

 * [Code of Conduct](CODE_OF_CONDUCT.md)
 * [JavaDocs](https://www.javadoc.io/doc/com.iopipe/iopipe)
 * [Writing and Using IOpipe Plugins for Java](PLUGINS.md)

In general:

 * [Java Programming Model on AWS](https://docs.aws.amazon.com/lambda/latest/dg/java-programming-model.html)
 * [Logging on AWS](https://docs.aws.amazon.com/lambda/latest/dg/java-logging.html)

