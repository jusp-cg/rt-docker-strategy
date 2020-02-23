#!/bin/sh

# ------------------------------------------------------------------------
# Build automation interface script
# ------------------------------------------------------------------------
CG_BUILD_SCRIPT_NAME=build.sh
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

# ------------------------------------------------------------------------
# Display parameters
# ------------------------------------------------------------------------

echo ------------------------------------------------------------------------
echo Parameters Passed to Script: ${CG_BUILD_SCRIPT_NAME}
echo ------------------------------------------------------------------------
echo Build Type                 : ${CG_BUILD_TYPE}
echo Build Number               : ${CG_BUILD_NUMBER}
echo Source Control Label Name  : ${CG_BUILD_LABEL}
echo Source Control last change : ${CG_SRC_LAST_CHANGE}
echo Application Name           : ${CG_APP_NAME}
echo Application Version        : ${CG_APP_VERSION}
echo Build Tools Home           : ${CG_TOOLS_HOME}
echo Running Build Step         :
echo ------------------------------------------------------------------------

# ------------------------------------------------------------------------
#  Create version_build.txt file with all environment variables
# ------------------------------------------------------------------------
echo BuildType=${CG_BUILD_TYPE} > version_build.txt
echo BuildNumber=${CG_BUILD_NUMBER} >> version_build.txt
echo SourceControlLabelName=${CG_BUILD_LABEL} >> version_build.txt
echo SourceControlLastChange=${CG_SRC_LAST_CHANGE} >> version_build.txt
echo ApplicationName=${CG_APP_NAME} >> version_build.txt
echo ApplicationVersion=${CG_APP_VERSION} >> version_build.txt
echo BuildToolsHome=${CG_TOOLS_HOME} >> version_build.txt
echo RunningBuildStep= >> version_build.txt
# ------------------------------------------------------------------------
#  Insert code below
# ------------------------------------------------------------------------

# ------------------------------------------------------------------------
# Build the solution
# ------------------------------------------------------------------------
# CG_BUILD_TYPE set within ./cgbuild/property/dev.properties (<branchname>.build.type)
BUILD_RC=0

# Setting the Maven Build Version
RELEASE_VERSION=""
if [[ ${bamboo_planRepository_branch} == *"release"* ]]
then
  RELEASE_VERSION="-Drevision=${CG_APP_VERSION}.${CG_BUILD_NUMBER}"
fi

cd ../dcip-ui

${NODEJS_HOME}/bin/npm -v

echo ------------------------------------------------------------------------
echo Start Install Node Modules
echo ------------------------------------------------------------------------

${NODEJS_HOME}/bin/npm ci

echo ------------------------------------------------------------------------
echo Start Install Node Modules with ---registry=https://registry.npmjs.org/
echo ------------------------------------------------------------------------

${NODEJS_HOME}/bin/npm ci --registry=https://registry.npmjs.org/

echo ------------------------------------------------------------------------
echo Start Building UI
echo ------------------------------------------------------------------------
${NODEJS_HOME}/bin/npm run build

echo ------------------------------------------------------------------------
echo Start tests UI
echo ------------------------------------------------------------------------

# ${NODEJS_HOME}/bin/npm test

${NODEJS_HOME}/bin/npm run jest


cd dist
echo ------------------------------------------------------------------------
echo Current directory ${PWD}
echo ------------------------------------------------------------------------

jar cf ../../java/libs/dcip-ui.jar public

# ----------------------------------------------
# Maven Build Command for Producer Project
# ----------------------------------------------
#echo "Release Version: ${RELEASE_VERSION}"
#echo "Running: ${M3_HOME}/bin/mvn clean install ${RELEASE_VERSION}"
#${M3_HOME}/bin/mvn clean install ${RELEASE_VERSION}

# ----------------------------------------------
# Maven Build Command for Standard Project
# ----------------------------------------------
cd ../../java
export JAVA_HOME=/users/bbcur/BuildTools/c/linux32/Sun/jdk/1.8.0_144
export project_root=`pwd`
${M3_HOME}/bin/mvn -Drelease.version=${CG_APP_VERSION} clean install


# Set the system status code
# BUILD_RC=$?

if [ ${CG_BUILD_TYPE} == 'rel' ]
then
  echo Build Type is ${CG_BUILD_TYPE}
  # build with release flags
    BUILD_RC=$?
fi

if [ ${CG_BUILD_TYPE} == 'dev' ]
then
  echo Build Type is ${CG_BUILD_TYPE}
  # build with debug flags
    BUILD_RC=$?
fi


# ------------------------------------------------------------------------
# Copy the needed artifacts from the build and publish them
# ------------------------------------------------------------------------
if [ ${CG_BUILD_TYPE} == 'rel' ]
then
    echo Copy release artifacts
    # copy release build binaries to cgbuild/dist/package
fi

if [ ${CG_BUILD_TYPE} == 'dev' ]
then
    echo Copy release artifacts
    # copy debug build binaries to cgbuild/dist/package
fi


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