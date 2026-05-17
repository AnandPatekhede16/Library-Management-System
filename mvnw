#!/bin/sh
# ----------------------------------------------------------------------------
# Licensed to the Apache Software Foundation (ASF) under one
# or more contributor license agreements. See the NOTICE file
# distributed with this work for additional information
# regarding copyright ownership. The ASF licenses this file
# to you under the Apache License, Version 2.0 (the
# "License"); you may not use this file except in compliance
# with the License. You may obtain a copy of the License at
#
#    https://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing,
# software distributed under the License is distributed on an
# "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
# KIND, either express or implied. See the License for the
# specific language governing permissions and limitations
# under the License.
# ----------------------------------------------------------------------------

# ----------------------------------------------------------------------------
# Apache Maven Wrapper startup batch script, version 3.3.2
# ----------------------------------------------------------------------------

set -e

# Find the project base dir, i.e. the closest directory that contains a ".mvn" directory.
find_maven_basedir() {
  if [ -z "$1" ]; then
    echo "Path not specified to find_maven_basedir"
    return 1
  fi

  basedir="$1"
  wdir="$1"
  while [ "$wdir" != '/' ]; do
    if [ -d "$wdir"/.mvn ]; then
      basedir=$wdir
      break
    fi
    # workaround for JBEAP-8937 (8.0.0.GA-redhat-00001)
    wdir=$(
      cd "$wdir/.."
      pwd
    )
  done
  printf '%s' "$(cd "$basedir" && pwd -P)"
}

# concatenates all lines of a file
concat_lines() {
  if [ -f "$1" ]; then
    echo "$(tr -s '\n' ' ' <"$1")"
  fi
}

log() {
  if [ "$MVNW_VERBOSE" = true ]; then
    printf '%s\n' "$1"
  fi
}

BASE_DIR=$(find_maven_basedir "$(dirname "$0")")

##########################################################################################
# Extension to allow automatically downloading the maven-wrapper.jar from Maven-central
# This allows using the maven wrapper in projects that prohibit checking in binary data.
##########################################################################################
wrapperJarPath="$BASE_DIR/.mvn/wrapper/maven-wrapper.jar"

if [ -r "$wrapperJarPath" ]; then
  log "Found $wrapperJarPath"
else
  log "Couldn't find $wrapperJarPath, downloading it ..."
  jarUrl="https://repo.maven.apache.org/maven2/org/apache/maven/wrapper/maven-wrapper/3.3.2/maven-wrapper-3.3.2.jar"
  while IFS="=" read -r key value; do
    # Remove '\r' from value
    value=$(echo "$value" | tr -d '\r')
    case "$key" in
    distributionUrl) distributionUrl=$value ;;
    wrapperUrl) jarUrl=$value ;;
    esac
  done <"$BASE_DIR/.mvn/wrapper/maven-wrapper.properties"

  log "Downloading from: $jarUrl"

  if $javaCmd -version >/dev/null 2>&1; then
    javaHome="$(dirname "$(dirname "$(command -v java)")")"
  fi

  if command -v wget >/dev/null; then
    log "Found wget ... using wget"
    [ "$MVNW_VERBOSE" = true ] && QUIET="" || QUIET="-q"
    if [ -z "$MVNW_USERNAME" ] || [ -z "$MVNW_PASSWORD" ]; then
      wget $QUIET "$jarUrl" -O "$wrapperJarPath" || rm -f "$wrapperJarPath"
    else
      wget $QUIET --http-user="$MVNW_USERNAME" --http-password="$MVNW_PASSWORD" "$jarUrl" -O "$wrapperJarPath" || rm -f "$wrapperJarPath"
    fi
  elif command -v curl >/dev/null; then
    log "Found curl ... using curl"
    [ "$MVNW_VERBOSE" = true ] && QUIET="" || QUIET="--silent"
    if [ -z "$MVNW_USERNAME" ] || [ -z "$MVNW_PASSWORD" ]; then
      curl $QUIET -o "$wrapperJarPath" "$jarUrl" -f
    else
      curl $QUIET --user "$MVNW_USERNAME:$MVNW_PASSWORD" -o "$wrapperJarPath" "$jarUrl" -f
    fi
  else
    log "Falling back to downloading with the JDK"
    if [ -d "$JAVA_HOME" ]; then
      javaHome="$JAVA_HOME"
    else
      javaHome="$(dirname "$(dirname "$(command -v java)")")"
    fi
    "$javaHome/bin/java" -classpath "" org.apache.maven.wrapper.MavenWrapperMain "$jarUrl" "$wrapperJarPath"
  fi
fi

# Start the wrapper
exec "$BASE_DIR/.mvn/wrapper/MavenWrapperMain" "$@"
MAVEN_PROJECTBASEDIR=${MAVEN_BASEDIR:-$BASE_DIR}

exec "$MAVEN_PROJECTBASEDIR/.mvn/wrapper/MavenWrapperMain" \
  -Dmaven.multiModuleProjectDirectory="$MAVEN_PROJECTBASEDIR" \
  "$@"
