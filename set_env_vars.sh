#!/bin/bash

# Script to set environment variables for DevHack application
# Usage: source set_env_vars.sh [environment]
# Where environment is one of: default, local, docker (default is "default")

# Exit if any command fails
set -e

# Default environment
ENV=${1:-default}

echo "Setting environment variables for $ENV environment..."

# Common variables (used across all environments)
export OPENAI_API_KEY="your-openai-api-key"
export OPENAI_MODEL="gpt-3.5-turbo"
export OPENAI_MAX_TOKENS="500"
export GPTJ_API_KEY="your-key"
export GPTJ_MAX_TOKENS="500"

# Environment-specific variables
if [ "$ENV" = "local" ]; then
    # Local environment variables
    export LOCAL_DB_URL="jdbc:postgresql://localhost:5434/interview_prep"
    export LOCAL_DB_USERNAME="postgres"
    export LOCAL_DB_PASSWORD="postgres"
    export GPTJ_MODEL="gpt-4"
    export GPTJ_API_URL_LOCAL="http://0.0.0.0:8086/v1/chat/completions"
    export LOCAL_AI_SERVICE_PROVIDER="gptj"
    
    echo "Local environment variables set."
    echo "To run the application with these variables:"
    echo "  ./gradlew bootRun --args='--spring.profiles.active=local'"

elif [ "$ENV" = "docker" ]; then
    # Docker environment variables
    export DOCKER_DB_URL="jdbc:postgresql://postgres:5432/interview_prep"
    export DOCKER_DB_USERNAME="postgres"
    export DOCKER_DB_PASSWORD="postgres"
    export GPTJ_MODEL="gpt-j-6b"
    export GPTJ_API_URL_DOCKER="http://gptj:8080/v1/chat/completions"
    export DOCKER_AI_SERVICE_PROVIDER="gptj"
    
    echo "Docker environment variables set."
    echo "To run the application with these variables:"
    echo "  docker-compose up -d"

else
    # Default environment variables
    export DB_URL="jdbc:postgresql://postgres:5432/interview_prep"
    export DB_USERNAME="postgres"
    export DB_PASSWORD="postgres"
    export GPTJ_MODEL="gpt-j-6b"
    export GPTJ_API_URL="http://gptj:8080/v1/chat/completions"
    export AI_SERVICE_PROVIDER="openai"
    
    echo "Default environment variables set."
    echo "To run the application with these variables:"
    echo "  ./gradlew bootRun"
fi

# Display instructions for updating sensitive values
echo ""
echo "IMPORTANT: Update the API keys with your actual values:"
echo "  export OPENAI_API_KEY=\"your-actual-openai-api-key\""
echo "  export GPTJ_API_KEY=\"your-actual-gptj-api-key\""
echo ""
echo "Remember to run this script with 'source' to export variables to your current shell:"
echo "  source set_env_vars.sh [environment]"