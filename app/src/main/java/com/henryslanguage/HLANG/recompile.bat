@echo off
javac *.java

mkdir dist

jar --create ^
    --file dist/HLANG.jar ^
    --main-class HSH ^
    *.class

jpackage ^
  --type app-image ^
  --name HLANGCompiler ^
  --input dist ^
  --main-jar HLANG.jar ^
  --main-class HSH ^
  --win-console

move "HLANGCompiler" "../Windows"

removeclasses