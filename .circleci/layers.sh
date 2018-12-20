#!/bin/sh -e

# Package JAR and place all dependencies into the target
mvn clean
mvn package -Dmaven.test.skip=true
mvn dependency:copy-dependencies -DincludeScope=runtime -DoutputDirectory=target

exit 7

