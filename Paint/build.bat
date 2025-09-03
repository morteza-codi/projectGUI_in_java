@echo off
echo Creating output directory...
if not exist "out\production\Paint" mkdir out\production\Paint

echo Compiling Java files...
javac -d out\production\Paint src\*.java

if %ERRORLEVEL% EQU 0 (
    echo Compilation successful!
    echo To run the application, use: java -cp out\production\Paint Main
    
    set /p choice="Do you want to run the application now? (y/n): "
    if /i "%choice%"=="y" (
        java -cp out\production\Paint Main
    )
) else (
    echo Compilation failed!
)

pause
