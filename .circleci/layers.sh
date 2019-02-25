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
rm -vf java/lib/*-sources.jar java/lib/*-javadoc.jar

# ZIP it up
rm -f java8.zip
zip -rq java8.zip java

# Unique identification for this release
__gh="$(git rev-parse HEAD)"

# The S3 file key
__s3="iopipe-java8/$__gh.zip"

# Upload for each region
for __region in ap-northeast-1 ap-northeast-2 ap-south-1 ap-southeast-1 ap-southeast-2 ca-central-1 eu-central-1 eu-west-1 eu-west-2 eu-west-3 us-east-1 us-east-2 us-west-1 us-west-2
do
	# The destination bucket, since it is region specific
	__bn="iopipe-layers-$__region"
	
	# Upload to S3 bucket first
	echo "Uploading to S3 in region $__region..."
	aws --region "$__region" s3 cp java8.zip "s3://$__bn/$__s3"
	
	# Publish layer, but we need the version
	echo "Publishing..."
	__ver="`aws lambda publish-layer-version \
		--layer-name IOpipeJava8 \
		--content "S3Bucket=$__bn,S3Key=$__s3" \
		--description 'IOpipe Layer Java 8' \
		--compatible-runtimes java8 \
		--license-info 'Apache 2.0' \
		--region $__region \
		--output text \
		--query Version`"
	if [ -z "$__ver" ]
	then
		echo "Failed to publish!"
		exit 2
	fi
	echo "Published version $__ver"
	
	# Set permissions
	echo "Setting permissions..."
	aws lambda add-layer-version-permission \
		--layer-name IOpipeJava8 \
		--version-number "$__ver" \
		--statement-id public \
		--action lambda:GetLayerVersion \
		--principal "*" \
		--region "$__region"
	echo "Permissions set"
done

