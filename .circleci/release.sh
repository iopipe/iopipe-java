#!/bin/sh -x
# This script handles the release process and is able to mock the current
# revision as a release

# Temporary directory to use
__tempdir="/tmp/iopipe-temp-$$"

# Call seld
"$(dirname -- "$0")/.dorelease.sh" "$__tempdir"
__eval="$?"

# Note the return value
echo "Sub-function returned with exit status $__eval" 1>&2

# Do not leave files sitting around in the temporary directory at all
echo "Cleaning up..." 1>&2
if ! rm -rf "$__tempdir"
then
	echo "FATAL: Failed to cleanup!" 1>&2
	exit 99
fi

# Exit with the error code of this script
exit $__eval

