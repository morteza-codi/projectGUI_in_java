#!/bin/bash

# Create output directory if it doesn't exist
mkdir -p out/production/Paint

# Compile all Java files
echo "Compiling Java files..."
javac -d out/production/Paint src/*.java

# Check if compilation was successful
if [ $? -eq 0 ]; then
    echo "Compilation successful!"
    echo "To run the application, use: java -cp out/production/Paint Main"
    
    # Ask if user wants to run the application
    read -p "Do you want to run the application now? (y/n): " choice
    if [[ $choice == "y" || $choice == "Y" ]]; then
        java -cp out/production/Paint Main
    fi
else
    echo "Compilation failed!"
fi
