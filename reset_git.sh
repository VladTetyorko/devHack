#!/bin/bash

# Script to reset git history while preserving all code
# This will remove all commits and start with a fresh git repository

# Exit on error
set -e

echo "Creating backup of the current code..."
BACKUP_DIR="../DevHack_backup_$(date +%Y%m%d_%H%M%S)"
mkdir -p "$BACKUP_DIR"
cp -r . "$BACKUP_DIR"
echo "Backup created at $BACKUP_DIR"

echo "Removing .git directory..."
rm -rf .git

echo "Initializing new git repository..."
git init

echo "Adding all files (except those in .gitignore)..."
git add .

echo "Making initial commit..."
git commit -m "Initial commit"

echo "Git history has been reset successfully."
echo "The .properties files with sensitive information are now excluded from git."
echo "Use the template files to create your own properties files with your API keys."
echo ""
echo "To verify, run: git status"
echo "You should see that the .properties files are not tracked."