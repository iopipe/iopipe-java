#!/bin/sh
# This script handles the release process and is able to mock the current
# revision as a release

run_release()
(
	# Temporary must be specified!
	if [ "$#" -lt "1" ]
	then
		echo "FATAL: Temporary directory not specified!" 1>&2
		exit 100
	fi
	
	# Always the first argument
	__tempdir="$1"

	# The repository lives here
	__baserepo="$(pwd)"

	# The entire release will be mocked in another directory
	# --no-local:     Git will hardlink files, we want actual copies so it does
	#                 not interact.
	# --no-hardlinks: Similar to above, do not use hardlines, treat as backup
	echo "Cloning into temporary directory..." 1>&2
	if ! git clone -v --no-local --no-hardlinks "$__baserepo" "$__tempdir"
	then
		echo "Failed to clone!" 1>&2
		exit 101
	fi

	# Go there
	cd "$__tempdir"

	# Try to determine the Java Version to release as
	__release_ver=""
	if [ -z "$JAVA_TAG" ]
	then
		echo "\$JAVA_TAG not specified, guessing the version from the POM..." 1>&2
		
		# This should extract the version although it could also break in
		# another locale, so hopefully it is not too troublesome
		__release_ver="`MAVEN_OPTS="-Dorg.slf4j.simpleLogger.defaultLogLevel=OFF
			-Dorg.slf4j.simpleLogger.log.org.apache.maven.plugins.help=INFO"
			mvn help:evaluate --batch-mode -Dexpression=project.version 2>&1 |
			grep -v '^\[INFO' | grep -v '^[dD]ownload' |
			grep '^[0-9]\{1,\}\.[0-9]\{1,\}\.[0-9]\{1,\}' |
			tail -n 1 | sed 's/[ \t]*//g'`"
		
		# Remove snapshot at the end of the version
		__release_ver="$(echo "$__release_ver" | sed 's/-SNAPSHOT$//')"
		echo "Guessed it was '$__release_ver'" 1>&2
	else
		# Only a single format is available
		if ! echo "$JAVA_TAG" | grep '^[0-9]\{1,\}\.[0-9]\{1,\}\.[0-9]\{1,\}\$'
		then
			echo "FATAL: \$JAVA_TAG is not in the correct format!" 1>&2
			echo "FATAL: '$JAVA_TAG' is not like '1.4.0'" 1>&2
			exit 103
		fi
		
		# Use the specified one
		__release_ver="$JAVA_TAG"
	fi
	
	# Sanity check the release version
	if ! echo "$__release_ver" | grep '^[0-9]\{1,\}\.[0-9]\{1,\}\.[0-9]\{1,\}$'
	then
		echo "FATAL: Release version is not in the correct format!" 1>&2
		echo "FATAL: '$__release_ver' is not like '1.4.0'" 1>&2
		exit 104
	fi
	
	# TODO
	exit 63

	# Success!!!
	exit 0
)

# Temporary directory to use
__tempdir="/tmp/iopipe-temp-$$"

# Call seld
run_release "$__tempdir"
__eval="$?"

# Note the return value
echo "Sub-function returned with exit status $__eval" 1>&2

# Do not leave files sitting around in the temporary directory at all
if ! rm -rf "$__tempdir"
then
	echo "FATAL: Failed to cleanup!" 1>&2
	exit 99
fi

# Exit with the error code of this script
exit $__eval

