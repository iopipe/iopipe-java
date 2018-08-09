# Writing and Using IOpipe Plugins for Java

This document contains information on how to write and use plugins for IOpipe.

 * [Using Plugins](#using-plugins)
   * [Detailed Instructions](#detailed-instructions)
 * [Writing Plugins](#writing-plugins)

For every invocation that is performed there is a unique object which
represents the state of the plugin for each plugin as they are needed. This
execution state is initialized when the plugin is first referenced. When the
method finishes execution, the state is removed and nothing more is done with
it. This mechanism can be used to keep track of which custom metrics should
be written to the report for example. The execution state is an implementation
of the class `com.iopipe.plugin.IOpipePluginExecution`. All operations within
a plugin are performed on the `IOpipePluginExecution` instance.

# Using Plugins

To quickly use a plugin:

 1. `import` the plugin execution state, this class will be used to refer to
    the plugin.
    * `import com.iopipe.examples.ExampleExecution;`
 2. Refer to the plugin via the `plugin` method and then use a lambda (or
    method reference) to the code you wish to execute. If a plugin is not
    enabled then the lambda will not be executed.
    * `__exec.<ExampleExecution>plugin(ExampleExecution.class, (__s) ->`
    * `    {`
    * `        __s.message("I shall say hello!");`
    * `        __s.message(__input);`
    * `    });`

## Detailed Instructions

All plugins are referred to by their execution state, which is an
implementation of the class `com.iopipe.plugin.IOpipePluginExecution`.

Each execution of a method is given a unique object which represents the
current execution. Plugins are accessed through that. Take for example a
plugin which is called `com.iopipe.examples.ExamplePlugin` and its
execution state is `com.iopipe.examples.ExampleExecution`. You would import
the following (note the second import is an exception in case there is no
available plugin):

```
import com.iopipe.examples.ExampleExecution;
import com.iopipe.plugin.NoSuchPluginException;
```

Now in the handler for your method, the passed instance of `IOpipeExecution`
in this example class will be called `__exec`. Obtaining the execution state
for the plugin is done as follows:

```
ExampleExecution example = __exec.<ExampleExecution>plugin(ExampleExecution.class);
```

If the plugin does not exist, then `NoSuchPluginException` will be thrown so
if your code needs to run regardless of whether the plugin works or not then
this should be caught and handled accordingly.

For convenience a lambda or method reference can be executed if the plugin is
enabled and is the recommended way to utilize plugins.

```
// Send a message to the example plugin
__exec.<ExampleExecution>plugin(ExampleExecution.class, (__s) ->
	{
		__s.message("I shall say hello!");
		__s.message(__input);
	});
```

If a plugin is optional and you wish `null` to be returned then a call to
`optionalPlugin` should be made instead.

# Writing Plugins

Plugins utilize the `ServiceLoader` class and therefore means that it is very
similar to writing other services. All plugins operate under the
`com.iopipe.plugin.IOpipePlugin` service and as such implementations of the
plugins must be referenced in the services file
`META-INF/services/com.iopipe.plugin.IOpipePlugin`.

All that is required that the interfaces be implemented and you will have a
functional plugin that may be used.

There are two additional interfaces which may be extended which make it so the
plugin with its execution state is called before and/or after a method has
executed, if it is required.

 * `com.iopipe.plugin.IOpipePluginPreExecutable`
   * The plugin is executed before the method begins.
 * `com.iopipe.plugin.IOpipePluginPostExecutable`
   * The plugin is executed after the method finishes.

Each plugin has its own execution state for each invocation which can be used
to store state along with providing functionality for the plugin if it can be
called within. The execution state implements
`com.iopipe.plugin.IOpipePluginExecution` and stores any of the state needed
for that single execution of a plugin.

