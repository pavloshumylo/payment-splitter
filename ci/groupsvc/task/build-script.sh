#!/bin/bash

export TERM=${TERM:-dumb}

cd source-code-from-github/GroupService
./gradlew build

cd ../..

mkdir group-svc-build/
cp source-code-from-github/GroupService/build/libs/GroupService-*.jar group-svc-build/
cp source-code-from-github/GroupService/build/publications/mavenJava/pom-default.xml group-svc-build/pom.xml