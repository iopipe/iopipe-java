# IOpipe Java Examples

The project contains examples for using Java lambdas with the IOpipe service.

These classes contain executable code:

 * `com.iopipe.examples.Hello`
   * Extends `com.iopipe.SimpleRequestHandlerWrapper`
 * `com.iopipe.examples.ManualHello`
   * Creates an instance of `IOpipeService` then invokes the lambda.
 * `com.iopipe.examples.Lowercase`
   * Extends `com.iopipe.SimpleRequestStreamHandlerWrapper`

The following are classes for the example plugin.

 * `com.iopipe.examples.ExamplePlugin`
   * Extends `com.iopipe.plugin.IOpipePlugin`
   * Allows creation of execution states for the example program.
 * `com.iopipe.examples.ExampleExecution`
   * Extends `com.iopipe.plugin.IOpipePluginExecution`
   * Stores the state for a single execution for the example plugin.

For reference the following classes exist which do not use the service or any
of the library code:

 * `com.iopipe.examples.PlainHello`
   * Implements `com.amazonaws.services.lambda.runtime.RequestHandler`.

