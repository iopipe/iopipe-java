#!/bin/sh -e

# Need this to publish
if ! which aws
then
	echo "No AWS command"
	exit 1
fi

# Package JAR and place all dependencies into the target
mvn clean
mvn package -Dmaven.test.skip=true
mvn dependency:copy-dependencies -DincludeScope=runtime -DoutputDirectory=target

# Copy target JARs to library directory
rm -rvf java
mkdir -p java/lib/
cp -v target/*.jar java/lib/

# ZIP it up
zip -rq java8.zip java

exit 7

