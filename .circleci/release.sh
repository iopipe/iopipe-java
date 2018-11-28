#!/bin/sh -x

# The API is documented here:
# https://www.jfrog.com/confluence/display/BT/Bintray+REST+API

# Environment variables used
if [ -z "$BINTRAY_USER" ]
then
	echo "BINTRAY_USER not set".
	exit 9
fi
if [ -z "$BINTRAY_APITOKEN" ]
then
	echo "BINTRAY_APITOKEN not set, this is your API key in your profile."
	exit 9
fi

# Export these
export BINTRAY_SUBJECT="iopipe"
export BINTRAY_REPO="iopipe"
export BINTRAY_PACKAGE="iopipe"

# We need node
if ! which node
then
	echo "NodeJS does not exist."
	exit 9
fi

# And curl
if ! which curl
then
	echo "curl does not exist."
	exit 9
fi

# And realpath
if ! which realpath
then
	echo "No realpath exists (from GNU Coreutils)"
	exit 9
fi

# Get the version from the POM
# This should extract the version although it could also break in
# another locale, so hopefully it is not too troublesome
__pom_ver="`cd ..; MAVEN_OPTS="-Dorg.slf4j.simpleLogger.defaultLogLevel=OFF
	-Dorg.slf4j.simpleLogger.log.org.apache.maven.plugins.help=INFO"
	mvn help:evaluate --batch-mode -Dexpression=project.version 2>&1 | \
	grep -v '^\[INFO' | grep -v '^[dD]ownload' | \
	grep '^[0-9]\{1,\}\.[0-9]\{1,\}\.[0-9]\{1,\}' | \
	tail -n 1 | sed 's/[ \t]*//g' | sed 's/-SNAPSHOT//g'`"

# Note it
echo "POM version: $__pom_ver" 1>&2

# Build 
mkdir -p "/tmp/$$/"
__pwd="$(pwd)"
if ! (cd .. && mvn deploy "-DaltDeploymentRepository=file::default::file:///tmp/$$/")
then
	echo "Failed to build distribution"
	exit 2
fi

# *** Create new version
# HTTP -> POST /packages/:subject/:repo/:package/versions
# BODY -> {
#   "name": "1.1.5",
#   "released": "ISO8601 (yyyy-MM-dd'T'HH:mm:ss.SSSZ)", (optional)
#   "desc": "This version...",
#   "github_release_notes_file": "RELEASE.txt", (optional)
#   "github_use_tag_release_notes": true, (optional)
#   "vcs_tag": "1.1.5" (optional)
# }
# RESULT -> Status: 201 Created
# {Version get JSON response}
if ! echo '{"name":"'$__pom_ver'", "desc":"'$__pom_ver'"}' | curl --data-binary @- -f -XPOST -u "$BINTRAY_USER:$BINTRAY_APITOKEN" -H "Content-Type: application/json" \
	"https://api.bintray.com/packages/$BINTRAY_SUBJECT/$BINTRAY_REPO/$BINTRAY_PACKAGE/versions"
then
	echo "Failed to create version!"
	exit 3
fi

# Go through every built file and upload them
# Ignore maven metadata because we do not care about those
for __file in $(find "/tmp/$$/" -type f | grep -v maven-metadata)
do
	# We operate on the relative path here, so we want to lose dist
	__as="$(realpath --relative-to="/tmp/$$/" "$__file")"
	
	# Now do the upload
	if ! ./binupload.js "$__file" "$__as"
	then
		echo "Failed to upload $__as"
		
		rm -rvf "/tmp/$$/"
		
		exit 4
	fi
done

# Do not need these files anymore
rm -rvf "/tmp/$$/"

# Publish everything
if ! ./binpublish.js "$__pom_ver"
then
	echo "Failed to publish!"
	exit 5
fi

# Maven central sync
if ! ./binmcsync.js "$__pom_ver"
then
	echo "Failed to sync to maven central!"
	exit 5
fi

