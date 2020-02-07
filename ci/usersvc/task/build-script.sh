#!/bin/bash

export TERM=${TERM:-dumb}

cd source-code-from-github/UserService
./gradlew build

cd ../..

mkdir user-svc-build/
cp source-code-from-github/UserService/build/libs/UserService-*.jar user-svc-build/
cp source-code-from-github/UserService/build/publications/mavenJava/pom-default.xml user-svc-build/pom.xml