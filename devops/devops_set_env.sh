#!/bin/bash

# export PATH=/data/bkdevops/apps/gradle/4.10.3/bin:$PATH
# export GRADLE_HOME=/data/bkdevops/apps/gradle/4.10.3/.

export PATH=/data/bkdevops/apps/android-sdk-linux/android-sdk_new/tools:$PATH
export ANDROID_HOME=/data/bkdevops/apps/android-sdk-linux/android-sdk_new/

export PATH=/data/bkdevops/apps/ndk/android-ndk-r21/.:$PATH
export ANDROID_NDK_HOME=/data/bkdevops/apps/ndk/android-ndk-r21/

# export PATH=/data/bkdevops/apps/maven/3.6.3/bin:$PATH
# export MAVEN_HOME=/data/bkdevops/apps/maven/3.6.3/.

echo "-------------check android build environment-----------"
echo "MAVEN_HOME: ${MAVEN_HOME}"
echo "GRADLE_HOME: ${GRADLE_HOME}"
echo "ANDROID_NDK_HOME: ${ANDROID_NDK_HOME}"
echo "ANDROID_HOME: ${ANDROID_HOME}"
echo "JAVA_HOME: ${JAVA_HOME}"
echo "PATH: ${PATH}"
echo "BK_CI_GIT_REPO_BRANCH: ${BK_CI_GIT_REPO_BRANCH}"
echo "-------------------------------------------------------"

echo "---------------set GIT_BRANCH -----------------"
setEnv "GIT_BRANCH" ${BK_CI_GIT_REPO_BRANCH}
echo "GIT_BRANCH: ${GIT_BRANCH}"
echo "-----------------------------------------------"