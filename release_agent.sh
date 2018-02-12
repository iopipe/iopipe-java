
curl -v -X POST -H 'Content-Type: application/json' -d "{
    \"build_parameters\": {
        \"MVN_RELEASE_TAG\": \"v$JAVA_TAG\",
        \"MVN_RELEASE_VER\": \"$JAVA_TAG\",
        \"MVN_RELEASE_DEV_VER\": \"$JAVA_TAG_NEXT-SNAPSHOT\",
        \"MVN_RELEASE_USER_EMAIL\": \"dev@iopipe.com\",
        \"MVN_RELEASE_USER_NAME\": \"Via CircleCI\"
    }
}" "https://circleci.com/api/v1/project/iopipe/iopipe-java/tree/master?circle-token=${CIRCLE_TOKEN}"
