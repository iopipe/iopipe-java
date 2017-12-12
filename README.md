# IOpipe Telemetry Agent for Java

 * <http://www.iopipe.com/>
 * Licensed under the Apache 2.0 License.

This project provides the capability of using the IOPipe service for AWS
lambda services.

# Project

## Requirements

This project requires Maven and at least Java 7.

## Structure

The project is structured into multiple sub-projects:

 * `iopipe-core` -- This is the base library and is required for everything
   to function properly.

## Building

Building requires Maven and a Java 7 Virtual Machine.

 * `mvn compile` -- Compile the project.
 * `mvn package` -- Compile JAR package.
 * `mvn test`    -- Run tests.
 * `mvn clean`   -- Clean build.

# Environment Variables

For compatibility with other IOPipe clients the following environment
variables are supported.

 * `IOPIPE_ENABLED`
   * If this is set and if the value is `True` (ignoring case) then the library
     will be enabled.
   * If this is set to `False`.
   * If this is not set then internally it is treated as being `True`.
 * `IOPIPE_TOKEN`
   * This represents the token of the IOPipe collector which is to obtain
     statistics.
   * This is the default token which will be used if no token was specified in
     the client.
   * If you need help looking for your token you can visit:
     [Find your project token](https://dashboard.iopipe.com/install).

