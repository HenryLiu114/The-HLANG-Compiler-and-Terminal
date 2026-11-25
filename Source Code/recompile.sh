#!/bin/bash

set -e

javac *.java

rm -rf dist
mkdir -p dist

jar --create \
    --file dist/HLANG.jar \
    --main-class HSH \
    *.class

rm -rf dist-app

jpackage \
  --type app-image \
  --name HLANGCompiler \
  --input dist \
  --main-jar HLANG.jar \
  --main-class HSH

mv HLANGCompiler ../Linux

rm -f *.class
