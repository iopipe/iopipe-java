#!/bin/sh

# The temporary directory where we want to clone to is the first argument
# to this script
if [ "$#" -lt "1" ]
then
	echo "FATAL: Temporary directory not specified!" 1>&2
	exit 100
fi
__tempdir="$1"

# The repository lives here
__baserepo="$(pwd)"

# The entire release will be mocked in another directory
# --no-local:     Git will hardlink files, we want actual copies so it does
#                 not interact.
# --no-hardlinks: Similar to above, do not use hardlines, treat as backup
echo "Cloning into temporary directory..." 1>&2
if ! git clone --no-local --no-hardlinks "$__baserepo" "$__tempdir"
then
	echo "Failed to clone!" 1>&2
	exit 101
fi

# Get the original push URL so we push to that instead
#__origurl="$(git config --get remote.origin.url)"
__origurl="$(git ls-remote --get-url)"
echo "Our remote is at: $__origurl" 1>&2

# Go there
cd "$__tempdir"

# Check the branch
git branch

# Set our remote to use the remote repository
echo "Temporary remote was: $(git ls-remote --get-url)" 1>&2
git remote set-url "$(git rev-parse --abbrev-ref --symbolic-full-name @{push} | cut -d '/' -f 1)" "$__origurl"
echo "Temporary remote is now: $(git ls-remote --get-url)" 1>&2

# Get the version from the POM
# This should extract the version although it could also break in
# another locale, so hopefully it is not too troublesome
__pom_ver="`MAVEN_OPTS="-Dorg.slf4j.simpleLogger.defaultLogLevel=OFF
	-Dorg.slf4j.simpleLogger.log.org.apache.maven.plugins.help=INFO"
	mvn help:evaluate --batch-mode -Dexpression=project.version 2>&1 | \
	grep -v '^\[INFO' | grep -v '^[dD]ownload' | \
	grep '^[0-9]\{1,\}\.[0-9]\{1,\}\.[0-9]\{1,\}' | \
	tail -n 1 | sed 's/[ \t]*//g' | sed 's/-SNAPSHOT//g'`"

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

# Default to these if these are not set!
: ${MVN_RELEASE_USER_EMAIL:="dev@iopipe.com"}
: ${MVN_RELEASE_USER_NAME:="Via CircleCI"}

# Set git configuration
git config user.email "${MVN_RELEASE_USER_EMAIL}"
git config user.name "${MVN_RELEASE_USER_NAME}"

# This is used because for now there is no key	
git config commit.gpgsign false

# Prefix for comments
__comment_prefix="[RELEASE $__release_ver] "

# Update the constants file
tr '\n' '\v' < src/main/java/com/iopipe/IOpipeConstants.java | \
	sed 's/\(AGENT_VERSION[ \t\v]*=[ \t\v]*"\)[0-9.]\{1,\}\(";\)/\1'"$__release_ver"'\2/g' | \
	tr '\v' '\n' > /tmp/$$
if [ ! -s /tmp/$$ ]
then
	echo "Failed to update constants file." 1>&2
	exit 109
fi

# Move it
mv -vf /tmp/$$ src/main/java/com/iopipe/IOpipeConstants.java
git add src/main/java/com/iopipe/IOpipeConstants.java

# Update the README too
tr '\n' '\v' < README.md | \
	sed 's/\(<[ \t\v]*dependency[ \t\v]*>[ \t\v]*<[ \t\v]*groupId[ \t\v]*>com\.iopipe<\/[ \t\v]*groupId[ \t\v]*>[ \t\v]*<[ \t\v]*artifactId[ \t\v]*>iopipe<\/[ \t\v]*artifactId[ \t\v]*>[ \t\v]*<[ \t\v]*version[ \t\v]*>\)[0-9]\{1,\}\.[0-9]\{1,\}\.[0-9]\{1,\}\(<\/version>[ \t\v]*<\/[ \t\v]*dependency[ \t\v]*>\)/\1'"$__release_ver"'\2/' | \
	tr '\v' '\n' > /tmp/$$
if [ ! -s /tmp/$$ ]
then
	echo "Failed to update the README." 1>&2
	exit 110
fi

# Move it
mv -vf /tmp/$$ README.md
git add README.md

# Record the update
git commit -m "${__comment_prefix}Update IOpipeConstants and README.md"

# Build the site and JavaDoc to make sure it works
if ! mvn --batch-mode site
then
	echo "Failed to build the Maven site!" 1>&2
	exit 111
fi

# Add everything in the docs directory
git add docs

# Commit that
git commit -m "${__comment_prefix}Update Maven Site and JavaDocs"

# Perform the release and such, creating new versions accordingly BUT
# do not push it to the remote repository!!
if ! mvn --batch-mode release:prepare -Dtag="v$__release_ver" \
	-DscmCommentPrefix="$__comment_prefix" \
	-DpushChanges=false \
	-DreleaseVersion="$__release_ver" \
	-DdevelopmentVersion="$__development_ver"
then
	echo "Failed to dry run the release prepare!" 1>&2
	exit 108
fi

# Git debug dumps
echo "*** GIT TAGS ***" 1>&2
git --no-pager tag -l

# Used to see what was done commit wise
seq 0 4 | sort -r | while read __i
do
	echo "*** BACKWARDS ~$__i ***" 1>&2
	echo "" 1>&2
	
	git --no-pager log -n 1 "HEAD~$__i"
	git --no-pager diff "HEAD~$(($__i + 1))" "HEAD~$__i" | head -n 100
done

# Want to really perform a release?
if [ "$DO_JAVA_RELEASE" = "pleaseperformarelease" ]
then
	# A nice big banner
	echo "******************************************************************" 1>&2
	echo "******************************************************************" 1>&2
	echo "*** WARNING A RELEASE IS ABOUT TO BE PERFORMED, THIS OPERATION ***" 1>&2
	echo "*** MIGHT NOT BE REVERSABLE. IF YOU WISH TO CANCEL THE RELEASE ***" 1>&2
	echo "*** THEN YOU HAVE THIRTY SECONDS TO CANCEL THE BUILD!          ***" 1>&2
	echo "******************************************************************" 1>&2
	echo "******************************************************************" 1>&2
	
	# Perform the countdown where the build can be stopped
	seq 0 30 | sort -g -r | while read __i
	do
		echo "Release in $__i second(s)..." 1>&2
		sleep 1
	done
	
	# Push to remote
	if ! git push --tags
	then
		echo "Failed to push changes to GIT!" 1>&2
		exit 116
	fi
	
	# Perform the actual release
	if ! mvn --batch-mode -s settings.xml release:perform
	then
		echo "Failed to perform the release!" 1>&2
		exit 115
	fi
fi

# Success!!!
exit 0

