#!/bin/bash

# -------------------
# 0. ROOT DIRECTORY CHECK
# -------------------

# Check if the script is being run from the root directory of the repo
if [ ! -f "settings.gradle.kts" ]; then
    echo "ERROR:"
    echo "Please run this script from the root directory of the repo."
    exit 1
fi

./gradlew :common:data:core:connectedDebugAndroidTest || exit 1
./gradlew :common:domain:connectedDebugAndroidTest || exit 1

