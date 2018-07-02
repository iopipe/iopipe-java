#!/bin/sh

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

# Get the version from the POM
# This should extract the version although it could also break in
# another locale, so hopefully it is not too troublesome
__pom_ver="`MAVEN_OPTS="-Dorg.slf4j.simpleLogger.defaultLogLevel=OFF \
	-Dorg.slf4j.simpleLogger.log.org.apache.maven.plugins.help=INFO" \
	mvn help:evaluate --batch-mode -Dexpression=project.version 2>&1 |
	grep -v '^\[INFO' | grep -v '^[dD]ownload' |
	grep '^[0-9]\{1,\}\.[0-9]\{1,\}\.[0-9]\{1,\}' |
	tail -n 1 | sed 's/[ \t]*//g' | sed 's/-SNAPSHOT//'`"

# Note it
echo "POM version: $__pom_ver" 1>&2

# Sanity check the POM version
if ! echo "$__pom_ver" | grep '^[0-9]\{1,\}\.[0-9]\{1,\}\.[0-9]\{1,\}$' > /dev/null
then
	echo "FATAL: POM version is not in the correct format!" 1>&2
	echo "FATAL: '$__pom_ver' is not like '1.4.0'" 1>&2
	exit 104
fi

# Split off
__pommaj="$(echo "$__pom_ver" | cut -d '.' -f 1)"
__pommin="$(echo "$__pom_ver" | cut -d '.' -f 2)"
__pomsub="$(echo "$__pom_ver" | cut -d '.' -f 3)"

# Try to determine the Java Version to release as
__release_ver=""
if [ -z "$JAVA_TAG" ]
then
	echo "\$JAVA_TAG not specified, using the version from the POM..." 1>&2
	__release_ver="$__pom_ver"
else
	# Only a single format is available
	if ! echo "$JAVA_TAG" | grep '^[0-9]\{1,\}\.[0-9]\{1,\}\.[0-9]\{1,\}$' > /dev/null
	then
		echo "FATAL: \$JAVA_TAG is not in the correct format!" 1>&2
		echo "FATAL: '$JAVA_TAG' is not like '1.4.0'" 1>&2
		exit 103
	fi
	
	# Use the specified one
	__release_ver="$JAVA_TAG"
fi

echo "Release version: $__release_ver" 1>&2

# Extract version fields
__relmaj="$(echo "$__release_ver" | cut -d '.' -f 1)"
__relmin="$(echo "$__release_ver" | cut -d '.' -f 2)"
__relsub="$(echo "$__release_ver" | cut -d '.' -f 3)"

# Determine the next development version
echo "Determining development version..." 1>&2
if [ -z "$JAVA_TAG_NEXT" ]
then
	echo "\$JAVA_TAG_NEXT not specified, using release version update" 1>&2
	
	# Just increment the sub number
	__development_ver="$__relmaj.$__relmin.$(($__relsub + 1))"
else
	# Only a single format is available
	if ! echo "$JAVA_TAG_NEXT" | grep '^[0-9]\{1,\}\.[0-9]\{1,\}\.[0-9]\{1,\}-SNAPSHOT$' > /dev/null
	then
		echo "FATAL: \$JAVA_TAG_NEXT is not in the correct format!" 1>&2
		echo "FATAL: '$JAVA_TAG_NEXT' is not like '1.5.0-SNAPSHOT'" 1>&2
		exit 105
	fi
	
	# Split off -SNAPSHOT
	__development_ver="$(echo "$JAVA_TAG_NEXT" | sed 's/-SNAPSHOT$//g')"
fi

# Note
echo "Development version: $__development_ver" 1>&2

# Split off
__devmaj="$(echo "$__development_ver" | cut -d '.' -f 1)"
__devmin="$(echo "$__development_ver" | cut -d '.' -f 2)"
__devsub="$(echo "$__development_ver" | cut -d '.' -f 3)"

# Sanity check to make sure the next version is newer, for simplicity
# just multiply the various fields to create a larger value
__pommath="$(expr '(' "$__pommaj" '*' 1000000 ')' + '(' "$__pommin" '*' 1000 ')' + "$__pomsub")"
__relmath="$(expr '(' "$__relmaj" '*' 1000000 ')' + '(' "$__relmin" '*' 1000 ')' + "$__relsub")"
__devmath="$(expr '(' "$__devmaj" '*' 1000000 ')' + '(' "$__devmin" '*' 1000 ')' + "$__devsub")"
if [ "$__relmath" -ge "$__devmath" ] || \
	[ "$__relmath" -lt "$__pommath" ] || \
	[ "$__devmath" -le "$__pommath" ]
then
	echo "New release or development version is older than another version!" 1>&2
	echo "POM: $__pom_ver; Release: $__release_ver; Development $__development_ver" 1>&2
	echo "These conditions must not be met!" 1>&2
	echo "Release     >= Development" 1>&2
	echo "Release     <  POM" 1>&2
	echo "Development <= POM" 1>&2
	exit 106
fi

# Make sure a dry run of the release will work!
if ! mvn --batch-mode -DdryRun=true release:prepare -Dtag="v$__release_ver" \
	-DreleaseVersion="$__release_ver" \
	-DdevelopmentVersion="$__development_ver-SNAPSHOT"
then
	echo "Failed to dry run the release prepare!" 1>&2
	exit 108
fi

# Git debug dumps
git --no-pager diff
git status

# This updates for the next development version
#if ! mvn --batch-mode release:update-versions \
#	-DreleaseVersion="$__release_ver" \
#	-DdevelopmentVersion="$__development_ver-SNAPSHOT"
#then
#	echo "Failed to update the POM version numbers!" 1>&2
#	exit 107
#fi
#
## Git debug dumps
#git --no-pager diff
#git status

# TODO
exit 63

# Success!!!
exit 0

