#!/bin/bash

# step 0: print env param
echo "BK_CI_GIT_REPO_BRANCH: ${BK_CI_GIT_REPO_BRANCH}"
echo "BK_CI_GIT_REPO_TAG: ${BK_CI_GIT_REPO_TAG}"
echo "BK_CI_BUILD_NUM: ${BK_CI_BUILD_NUM}"
echo "TEST_MAVEN: ${TEST_MAVEN}"
echo "VERSION: ${VERSION}"
echo "APP_VERSION: ${APP_VERSION}"

# step 1: init gradle param

if [ -z "${BK_CI_BUILD_NUM}" ]
then
  echo "BK_CI_BUILD_NUM is empty. ${BK_CI_BUILD_NUM}"
else
  GRADLE_PARAM="-PBUILD_ID=${BK_CI_BUILD_NUM}"
fi

if [ "${OFFICIAL}" = true ];then
    GRADLE_PARAM="${GRADLE_PARAM} -POFFICIAL"
fi

if [ "${TEST_MAVEN}" = true ];then
    GRADLE_PARAM="${GRADLE_PARAM} -PTEST_MAVEN"
    GRADLE_PARAM="${GRADLE_PARAM} -PVERSION=$VERSION"
fi

if [ -z "${APP_VERSION}" ]
then
    echo "APP_VERSION is empty. ${APP_VERSION}"
else
    GRADLE_PARAM="${GRADLE_PARAM} -PAPP_VERSION=$APP_VERSION"
fi

echo "GRADLE_PARAM: ${GRADLE_PARAM}"

# step 2: clean and build apk
./gradlew clean ${GRADLE_PARAM}
./gradlew app:assembleRelease  ${GRADLE_PARAM}