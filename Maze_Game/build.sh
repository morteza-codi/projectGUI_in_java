#!/bin/bash

# Build script for Maze Runner 3D

# Create output directory if it doesn't exist
mkdir -p out/production/Maze_Game

# Compile Java files
echo "Compiling Java files..."
javac -d out/production/Maze_Game src/*.java

# Check if compilation was successful
if [ $? -eq 0 ]; then
    echo "Compilation successful!"
    
    # Create JAR file
    echo "Creating JAR file..."
    cd out/production/Maze_Game
    jar cvf ../../../MazeRunner3D.jar *
    cd ../../../
    
    echo "Build complete! You can run the game with:"
    echo "java -cp out/production/Maze_Game Main"
    echo "Or using the JAR file:"
    echo "java -jar MazeRunner3D.jar"
else
    echo "Compilation failed!"
    exit 1
fi
