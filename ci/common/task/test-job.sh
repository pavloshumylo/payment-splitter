#!/bin/bash

export TERM=${TERM:-dumb}
cd source-code-from-github/common
./gradlew test