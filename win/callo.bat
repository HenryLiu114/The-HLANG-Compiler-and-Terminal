@echo off

if "%~1"=="" (
    echo Usage: callo ^<filename^>
    exit /b 1
)

echo. > "%~1.hlang"

echo Created %~1.hlang