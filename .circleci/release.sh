#!/bin/sh -x

# We need node
if ! which node
then
	echo "NodeJS does not exist."
	exit 9
fi

# And realpath
if ! which realpath
then
	echo "No realpath exists (from GNU Coreutils)"
	exit 9
fi

# Build 
mkdir -p "/tmp/$$/"
__pwd="$(pwd)"
if ! (cd .. && mvn deploy "-DaltDeploymentRepository=file::default::file:///tmp/$$/")
then
	echo "Failed to build distribution"
	exit 2
fi

# Create version first
if ! ./bincreatever.js
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
if ! ./binpublish.js
then
	echo "Failed to publish!"
	exit 5
fi

# Maven central sync
if ! ./binmcsync.js
then
	echo "Failed to sync to maven central!"
	exit 5
fi

