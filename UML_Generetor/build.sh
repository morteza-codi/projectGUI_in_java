#!/bin/bash

# Simple build script for UML Generator

# Create output directory if it doesn't exist
mkdir -p out/production/UML_Generetor

# Compile all Java files
echo "Compiling Java files..."
javac -d out/production/UML_Generetor src/*.java

# Check if compilation was successful
if [ $? -eq 0 ]; then
    echo "Compilation successful!"
    echo "To run the application, use: java -cp out/production/UML_Generetor UMLGenerator"
else
    echo "Compilation failed!"
    exit 1
fi
