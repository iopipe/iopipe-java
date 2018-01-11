# IOpipe Java Examples

The project contains examples for using Java lambdas with the IOpipe service.

It contains these classes:

 * `com.iopipe.examples.Hello`
   * Extends `com.iopipe.SimpleRequestHandlerWrapper`
 * `com.iopipe.examples.ManualHello`
   * Creates an instance of `IOpipeService` then invokes the lambda.
 * `com.iopipe.examples.Lowercase`
   * Extends `com.iopipe.SimpleRequestStreamHandlerWrapper`

For reference the following classes exist which do not use the service or any
of the library code:

 * `com.iopipe.examples.PlainHello`
   * Implements `com.amazonaws.services.lambda.runtime.RequestHandler`.

