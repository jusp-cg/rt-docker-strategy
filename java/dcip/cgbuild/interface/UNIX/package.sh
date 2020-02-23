#!/bin/ksh

# ------------------------------------------------------------------------
# Build automation interface script
# ------------------------------------------------------------------------
CG_BUILD_SCRIPT_NAME=package.sh
BUILD_RC=0

echo ------------------------------------------------------------------------
echo Start Script: ${CG_BUILD_SCRIPT_NAME}
echo ------------------------------------------------------------------------

# ------------------------------------------------------------------------
# Environment specific settings should be read from env.sh
# ------------------------------------------------------------------------
. ./env.sh

# ------------------------------------------------------------------------
# CD out of the cgbuild directory (./cgbuild/interface/UNIX) for
# the rest of the relative paths within this script to work
# ------------------------------------------------------------------------
cd ../../..

# for debugging
#ls -lrt

export project_root=`pwd`
#/users/bamboo-agent/bamboo-agent-home-4/xml-data/build-dir/DCIP-JAVA1-JOB1/java
# ------------------------------------------------------------------------
# Display parameters
# ------------------------------------------------------------------------

echo ------------------------------------------------------------------------
echo Parameters Passed to Script: ${CG_BUILD_SCRIPT_NAME}
echo ------------------------------------------------------------------------
echo Build Type                 : ${CG_BUILD_TYPE}
echo Build Number               : ${CG_BUILD_NUMBER}
echo Source Control Label Name  : ${CG_BUILD_LABEL}
echo Source Control Server      : ${CG_SRC_SERVER}
echo Source Control Client View : ${CG_SRC_SPEC}
echo Source Control last change : ${CG_SRC_LAST_CHANGE}
echo Application Name           : ${CG_APP_NAME}
echo Application Version        : ${CG_APP_VERSION}
echo Build Tools Home           : ${CG_TOOLS_HOME}
echo Build Package ID           : ${CG_BUILD_PKG_ID}
echo ------------------------------------------------------------------------


# ------------------------------------------------------------------------
#  Insert code below
# ------------------------------------------------------------------------

#new lines added

echo "Create packaging artifacts directories"

echo ${project_root}

[ ! -d ./cgbuild/dist/package ] && mkdir -p ./cgbuild/dist/package
[ ! -d ./cgbuild/dist/publish/artifacts/unix ] && mkdir -p ./cgbuild/dist/publish/artifacts/unix
[ ! -d ./cgbuild/dist/publish/artifacts/unix/content/ ] && mkdir -p ./cgbuild/dist/publish/artifacts/unix/content
#[ ! -d ./cgbuild/dist/publish/artifacts/unix/content/config ] && mkdir -p ./cgbuild/dist/publish/artifacts/unix/content/config
[ ! -d ./cgbuild/dist/publish/artifacts/content/pre-install ] && mkdir -p ./cgbuild/dist/publish/artifacts/content/pre-install
[ ! -d ./cgbuild/dist/publish/artifacts/content/post-install ] && mkdir -p ./cgbuild/dist/publish/artifacts/content/post-install
[ ! -d ./cgbuild/dist/reports ] && mkdir -p ./cgbuild/dist/reports
[ ! -d ./cgbuild/dist/reports/JUnitReports ] && mkdir -p ./cgbuild/dist/reports/JUnitReports
#/users/bamboo-agent/bamboo-agent-home-4/xml-data/build-dir/DCIP-JAVA1-JOB1/java
#Edit end

echo "Copying pre/post install scripts to publish dir"
# FIXME: Stop building UI in java folder
cp -R ${project_root}/reports/JUnitReports/* ./cgbuild/dist/reports/JUnitReports
cp -R ${project_root}/post-install ./cgbuild/dist/publish/artifacts/unix/
cp -R ${project_root}/pre-install ./cgbuild/dist/publish/artifacts/unix/
cp -R ${project_root}/service-config/* ./cgbuild/dist/publish/artifacts/unix/content
#Edit ends

echo "Copy artifacts to DICP Publish directories"

if [ ${bamboo_buildprops_package} -eq 0 ]
then
    echo Skipping Running Package Step
    exit 0
fi

echo "Running Build Step Commands"
BUILD_RC=0
echo ${CG_BUILD_PKG_ID} > ./cgbuild/dist/package/version.txt

# ----------------------------------------------
# Maven Package Command for Producer Project
# ----------------------------------------------
# Setting the Maven Build Version
RELEASE_VERSION=""
if [[ ${bamboo_planRepository_branch} == *"release"* ]]
then
  RELEASE_VERSION="-Drevision=${CG_APP_VERSION}.${CG_BUILD_NUMBER}"
fi
# Run Maven to upload the artifacts
mkdir -p ./dcip-main/target
cp ./cgbuild/dist/package/version.txt ./dcip-main/target
cp version_build.txt ./dcip-main/target
cd ./dcip-main/target

#new line added and existing modified
cp *.jar version_build.txt ../../cgbuild/dist/publish/artifacts/unix/content/
#cp ../src/main/resources/application.properties ../../cgbuild/dist/publish/artifacts/unix/content/config

cp version.txt ../../cgbuild/dist/publish/artifacts

cd ../../cgbuild/dist/publish/artifacts/unix
#/users/bamboo-agent/bamboo-agent-home-3/xml-data/build-dir/DCIP-JAVA1-JOB1/java/cgbuild/dist/publish/artifacts/unix
#Lines added
zip -r ${CG_BUILD_PKG_ID}.zip content post-install pre-install
#Edit ends

cp -r ${CG_BUILD_PKG_ID}.zip ../../../package


# Set the system status code
BUILD_RC=$?

if [ ${BUILD_RC} -ne 0 ]
then
  echo Build step failed
  echo ------------------------------------------------------------------------
  echo Finish script: ${CG_BUILD_SCRIPT_NAME}  Result: ${BUILD_RC}
  echo ------------------------------------------------------------------------
  exit 1
fi

echo Build step completed
echo ------------------------------------------------------------------------
echo Finish script: ${CG_BUILD_SCRIPT_NAME}  Result: ${BUILD_RC}
echo ------------------------------------------------------------------------

exit $?