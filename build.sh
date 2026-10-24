#!/bin/bash
set -e
mvn clean package
rm -rf build-input dist
mkdir build-input
cp target/kinship.jar build-input/

jpackage --type deb --name Kinship \
  --input build-input \
  --main-jar kinship.jar \
  --main-class com.mycompany.kinship.Kinship \
  --icon packaging/kinship.png \
  --app-version 1.0.0 \
  --linux-shortcut \
  --dest dist
