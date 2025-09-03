@echo off
REM Build script for Maze Runner 3D

REM Create output directory if it doesn't exist
if not exist out\production\Maze_Game mkdir out\production\Maze_Game

REM Compile Java files
echo Compiling Java files...
javac -d out\production\Maze_Game src\*.java

REM Check if compilation was successful
if %ERRORLEVEL% EQU 0 (
    echo Compilation successful!
    
    REM Create JAR file
    echo Creating JAR file...
    cd out\production\Maze_Game
    jar cvf ..\..\..\MazeRunner3D.jar *
    cd ..\..\..
    
    echo Build complete! You can run the game with:
    echo java -cp out\production\Maze_Game Main
    echo Or using the JAR file:
    echo java -jar MazeRunner3D.jar
) else (
    echo Compilation failed!
    exit /b 1
)
