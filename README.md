# IOpipe Telemetry Agent for Java

 * <http://www.iopipe.com/>
 * Licensed under the Apache 2.0 License.

This project provides analytics and distributed tracing for event-driven
applications running on AWS Lambda using the IOpipe service.

# Building And Installing The Project

This project requires at least Java 8 to run and additionally required Maven
to build.

 * `mvn compile`         -- Compile the project.
 * `mvn package`         -- Compile JAR package.
 * `mvn test`            -- Run tests.
 * `mvn clean`           -- Clean build.
 * `mvn install`         -- Install the project into your own Maven repository.
 * `mvn site`            -- Generate Maven informational pages.
 * `mvn javadoc:javadoc` -- Generate JavaDoc.

# System Properties And Environment Variables.

These are used to configure IOpipe from the outside environment or using the
configuration means which you currently use. Note that Amazon itself does not
currently support setting of system properties.

For compatibility with other IOpipe clients the environment variables (the
quoted names in UPPER_CASE_WITH_UNDERSCORE) are
supported, however system properties (which start with `com.iopipe`) are also
supported and take precedence.

 * `com.iopipe.debug` or `IOPIPE_DEBUG`
   * If this is set to `true` then
   * If this is not set then it defaults to `false`.
 * `com.iopipe.enabled` or `IOPIPE_ENABLED`
   * If this is set and if the value is `true` (ignoring case) then the library
     will be enabled.
   * If this is set to `false`.
   * If this is not set then internally it is treated as being `true`.
 * `com.iopipe.installmethod` or `IOPIPE_INSTALL_METHOD`
 * `com.iopipe.timeoutwindow` or `IOPIPE_TIMEOUT_WINDOW`
   * This time is subtracted from the duration that a lambda may operate on
     the service, if 
   * If this is zero then the window is disabled.
   * If this is not set then it defaults to `150`.
 * `com.iopipe.token`, `IOPIPE_TOKEN`, or `IOPIPE_CLIENTID`
   * This represents the token of the IOPipe collector which is to obtain
     statistics.
   * This is the default token which will be used if no token was specified in
     the client.
   * If you need help looking for your token you can visit:
     [Find your project token](https://dashboard.iopipe.com/install).

# Integration Into Your Maven Project With Amazon AWS Lambdas

Using the IOpipe service with your pre-existing and newly created classes is
quite simple. If you are using Maven is requires modification of your `pom.xml`
file, otherwise you may include the JAR file of the library to your project.

## Setting Up A Maven Project

Note that the instructions here are slightly derived from
<https://docs.aws.amazon.com/lambda/latest/dg/java-create-jar-pkg-maven-no-ide.html>,
however they have been slightly modified.

If you do not have a Maven project and you wish to start one, the recommended
way of doing so is to modify and execute the following:

```
mvn archetype:generate -DgroupId=com.mycompany.app \
 -DartifactId=my-app -DarchetypeArtifactId=maven-archetype-quickstart \
 -DinteractiveMode=false
```

In the `pom.xml`, add the following which will bring in the service library
and its dependencies:

```
<dependency>
  <groupId>com.iopipe</groupId>
  <artifactId>iopipe</artifactId>
  <version>1.0-SNAPSHOT</version>
</dependency>
```

And the following which will allow you to distribute a single JAR which may
then be uploaded to Amazon's services:

```
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

Once you have these two in your `pom.xml` you are ready to start using the
actual classes.

## Using The Classes

There are three ways to use the service:

 * Implement `com.iopipe.SimpleRequestHandlerWrapper`.
 * Implement `com.iopipe.SimpleRequestStreamHandlerWrapper`.
 * Using the service directly.

### Implement `com.iopipe.SimpleRequestHandlerWrapper`.

***TO BE WRITTEN***

### Implement `com.iopipe.SimpleRequestStreamHandlerWrapper`.

***TO BE WRITTEN***

### Using the service directly.

***TO BE WRITTEN***

## Building And Deploying

To create a package which is ready for deployment you may type the following
command:

 * `mvn package`

If you wish to strip all debugging information in the JAR file __including__
__potentially meaningful source lines to stack traces__ you can run the
following command:

`pack200 -r -G file.jar`

Deployment is the same for other Java programs.

