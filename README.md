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

