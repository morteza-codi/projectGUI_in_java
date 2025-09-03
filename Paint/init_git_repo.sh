#!/bin/bash

# Initialize Git repository
git init

# Add all files
git add .

# Make initial commit
git commit -m "Initial commit: Modern Paint Application"

# Instructions for GitHub
echo ""
echo "Repository initialized successfully!"
echo ""
echo "To push to GitHub, follow these steps:"
echo "1. Create a new repository on GitHub (without README, .gitignore, or LICENSE)"
echo "2. Run the following commands:"
echo "   git remote add origin https://github.com/yourusername/modern-paint.git"
echo "   git branch -M main"
echo "   git push -u origin main"
echo ""
echo "Replace 'yourusername' with your actual GitHub username."
