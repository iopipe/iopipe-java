#!/bin/sh

# This is when someone calls the script wrong
print_help()
{
	echo "Usage: $0 [-a token] [-d version] (-h) (-r version) (-y)" 1>&2
	echo "    -a token  : CircleCI authorization token." 1>&2
	echo "    -d version: Development version (ex: '1.2.4')" 1>&2
	echo "    -h        : Print help." 1>&2
	echo "    -r version: Optional release version (ex: '1.2.3')." 1>&2
	echo "    -y        : Ignore a missing development version and use the next sub-release." 1>&2
}

# No arguments?
if [ "$#" -le "0" ]
then
	print_help
	exit 5
fi

# These parameters are needed for it to work
__rel=""
__dev=""
__tok=""
__donotreallycare="0"
while getopts r:d:a:yh __opt
do
	case $__opt in
			# Release version
		r)
			__rel="$OPTARG"
			;;
			
			# Development version
		d)
			__dev="$OPTARG"
			;;
			
			# CircleCI authorization token
		a)
			__tok="$OPTARG"
			;;
		
			# We just do not care about warnings, let us just release anyway
		y)
			__donotreallycare="1"
			;;
		
			# Help
		?)
		h)
			print_help
			exit 2
	esac
done

# Just do it anyway even if a version was not specified?
if [ -z "$__donotreallycare" ] || [ "$__donotreallycare" = "0" ]
then
	if [ -z "$__dev" ]
	then
		echo "No development version was specified." 1>&2
		echo "If not specified the next development version will a" 1>&2
		echo "the smallest version increment of the current release." 1>&2
		echo "If you really want to do this specify -y." 1>&2
		exit 4
	fi
fi

# This is required to perform the call
if [ -z "$__tok" ]
then
	echo "A CircleCI authorization token must be specified!" 1>&2
	exit 3
fi

# Perform the API call
curl -v -X POST -H 'Content-Type: application/json' -d "{
    \"build_parameters\": {
        \"DO_JAVA_RELEASE\": \"pleaseperformarelease\",
        \"JAVA_TAG\": \"$JAVA_TAG\",
        \"JAVA_TAG_NEXT\": \"$JAVA_TAG_NEXT\",
        \"MVN_RELEASE_TAG\": \"v$JAVA_TAG\",
        \"MVN_RELEASE_VER\": \"$JAVA_TAG\",
        \"MVN_RELEASE_DEV_VER\": \"$JAVA_TAG_NEXT-SNAPSHOT\",
        \"MVN_RELEASE_USER_EMAIL\": \"dev@iopipe.com\",
        \"MVN_RELEASE_USER_NAME\": \"Via CircleCI\"
    }
}" "https://circleci.com/api/v1/project/iopipe/iopipe-java/tree/master?circle-token=$__tok"

