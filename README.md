# IOpipe Telemetry Agent for Java

 * <http://www.iopipe.com/>
 * Licensed under the Apache 2.0 License.

This project provides the capability of using the IOPipe service for AWS
lambda services.

# Project

## Requirements

This project requires Maven and at least Java 7.

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

# Integration Into Your Maven Project With Amazon AWS Lambdas

***TO BE WRITTEN COMPLETELY***

The instructions here for setting up your Maven project are similar to the ones
listed on the Amazon site:
<https://docs.aws.amazon.com/lambda/latest/dg/java-create-jar-pkg-maven-no-ide.html>.
There are however differences since your lambda will need to be wrapped
accordingly for it to function and record any statistics.

In your `pom.xml` have the following

```
<dependency>
  <groupId>com.iopipe</groupId>
  <artifactId>iopipe</artifactId>
  <version>1.0-SNAPSHOT</version>
</dependency>

<build>
  <plugins>
    <plugin>
      <groupId>org.apache.maven.plugins</groupId>
      <artifactId>maven-shade-plugin</artifactId>
      <version>2.3</version>
      <configuration>
        <createDependencyReducedPom>false</createDependencyReducedPom>
      </configuration>
      <executions>
        <execution>
          <phase>package</phase>
          <goals>
            <goal>shade</goal>
          </goals>
        </execution>
      </executions>
    </plugin>
  </plugins>
</build>
```

***TO BE WRITTEN COMPLETELY***

