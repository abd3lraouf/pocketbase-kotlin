#!/bin/bash

# Smart Commit Script with Emoji and Best Practices
# Usage: ./scripts/commit.sh <type> <scope> <description> [body-points...]

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
PURPLE='\033[0;35m'
CYAN='\033[0;36m'
NC='\033[0m' # No Color

# Function to get emoji for commit type
get_emoji() {
    case "$1" in
        "feat") echo "✨" ;;
        "fix") echo "🐛" ;;
        "docs") echo "📚" ;;
        "style") echo "💄" ;;
        "refactor") echo "♻️" ;;
        "perf") echo "⚡" ;;
        "test") echo "🧪" ;;
        "build") echo "👷" ;;
        "ci") echo "🔧" ;;
        "chore") echo "🔨" ;;
        "revert") echo "⏪" ;;
        "merge") echo "🔀" ;;
        "init") echo "🎉" ;;
        "security") echo "🔒" ;;
        "deps") echo "📦" ;;
        "config") echo "⚙️" ;;
        "i18n") echo "🌐" ;;
        "typo") echo "✏️" ;;
        "wip") echo "🚧" ;;
        "remove") echo "🗑️" ;;
        "add") echo "➕" ;;
        "update") echo "⬆️" ;;
        "downgrade") echo "⬇️" ;;
        "pin") echo "📌" ;;
        "analytics") echo "📈" ;;
        "deploy") echo "🚀" ;;
        "docker") echo "🐳" ;;
        "k8s") echo "☸️" ;;
        "mobile") echo "📱" ;;
        "responsive") echo "📱" ;;
        "accessibility") echo "♿" ;;
        "seo") echo "🔍" ;;
        "database") echo "🗃️" ;;
        "logs") echo "📝" ;;
        "error") echo "💥" ;;
        "catch") echo "🥅" ;;
        "release") echo "🏷️" ;;
        "hotfix") echo "🚑" ;;
        "review") echo "👀" ;;
        "mock") echo "🤡" ;;
        "egg") echo "🥚" ;;
        "ignore") echo "🙈" ;;
        "snapshot") echo "📸" ;;
        "experiments") echo "⚗️" ;;
        "search") echo "🔍" ;;
        "flags") echo "🚩" ;;
        "animation") echo "💫" ;;
        "validation") echo "✅" ;;
        "types") echo "🏷️" ;;
        "api") echo "🔌" ;;
        "webhook") echo "🪝" ;;
        "auth") echo "🔐" ;;
        "license") echo "📄" ;;
        "backup") echo "💾" ;;
        "restore") echo "♻️" ;;
        "monitoring") echo "📊" ;;
        "health") echo "🏥" ;;
        "pwa") echo "📱" ;;
        "ux") echo "👥" ;;
        "ui") echo "🎨" ;;
        "assets") echo "🖼️" ;;
        "fonts") echo "🔤" ;;
        "cleanup") echo "🧹" ;;
        *) echo "📝" ;;
    esac
}

# Function to check if commit type is valid
is_valid_type() {
    case "$1" in
        "feat"|"fix"|"docs"|"style"|"refactor"|"perf"|"test"|"build"|"ci"|"chore"|"revert"|"merge"|"init"|"security"|"deps"|"config"|"i18n"|"typo"|"wip"|"remove"|"add"|"update"|"downgrade"|"pin"|"analytics"|"deploy"|"docker"|"k8s"|"mobile"|"responsive"|"accessibility"|"seo"|"database"|"logs"|"error"|"catch"|"release"|"hotfix"|"review"|"mock"|"egg"|"ignore"|"snapshot"|"experiments"|"search"|"flags"|"animation"|"validation"|"types"|"api"|"webhook"|"auth"|"license"|"backup"|"restore"|"monitoring"|"health"|"pwa"|"ux"|"ui"|"assets"|"fonts"|"cleanup")
            return 0
            ;;
        *)
            return 1
            ;;
    esac
}

# Valid commit types list
VALID_TYPES="feat fix docs style refactor perf test build ci chore revert merge init security deps config i18n typo wip remove add update downgrade pin analytics deploy docker k8s mobile responsive accessibility seo database logs error catch release hotfix review mock egg ignore snapshot experiments search flags animation validation types api webhook auth license backup restore monitoring health pwa ux ui assets fonts cleanup"

# Function to display usage
usage() {
    echo -e "${BLUE}Usage: $0 <type> <scope> <description> [body-points...]${NC}"
    echo ""
    echo -e "${YELLOW}Available commit types:${NC}"
    echo "  feat      - A new feature"
    echo "  fix       - A bug fix"
    echo "  docs      - Documentation only changes"
    echo "  style     - Changes that do not affect the meaning of the code"
    echo "  refactor  - A code change that neither fixes a bug nor adds a feature"
    echo "  perf      - A code change that improves performance"
    echo "  test      - Adding missing tests or correcting existing tests"
    echo "  build     - Changes that affect the build system or external dependencies"
    echo "  ci        - Changes to our CI configuration files and scripts"
    echo "  chore     - Other changes that don't modify src or test files"
    echo "  revert    - Reverts a previous commit"
    echo ""
    echo -e "${YELLOW}Examples:${NC}"
    echo "  $0 feat auth \"add OAuth2 support\" \"Add Google OAuth2 provider\" \"Add GitHub OAuth2 provider\" \"Update auth documentation\""
    echo "  $0 fix api \"resolve null pointer exception\" \"Fix null check in user service\" \"Add proper error handling\""
    echo "  $0 docs readme \"update installation guide\" \"Add Kotlin Multiplatform setup\" \"Update dependency versions\""
    echo ""
    echo -e "${YELLOW}Scope examples:${NC}"
    echo "  auth, api, ui, docs, build, test, config, etc."
}

# Function to validate commit type
validate_type() {
    local type="$1"
    if ! is_valid_type "$type"; then
        echo -e "${RED}Error: Invalid commit type '$type'${NC}"
        echo -e "${YELLOW}Valid types: $VALID_TYPES${NC}"
        return 1
    fi
}

# Function to validate inputs
validate_inputs() {
    if [[ $# -lt 3 ]]; then
        echo -e "${RED}Error: Missing required arguments${NC}"
        usage
        return 1
    fi
    
    local type="$1"
    local scope="$2"
    local description="$3"
    
    validate_type "$type" || return 1
    
    if [[ -z "$scope" ]]; then
        echo -e "${RED}Error: Scope cannot be empty${NC}"
        return 1
    fi
    
    if [[ -z "$description" ]]; then
        echo -e "${RED}Error: Description cannot be empty${NC}"
        return 1
    fi
    
    # Check description length (recommended max 50 chars for subject)
    if [[ ${#description} -gt 50 ]]; then
        echo -e "${YELLOW}Warning: Description is longer than 50 characters (${#description}). Consider shortening it.${NC}"
    fi
    
    # Check if description starts with capital letter
    if [[ ! "$description" =~ ^[A-Z] ]]; then
        echo -e "${YELLOW}Warning: Description should start with a capital letter${NC}"
    fi
    
    # Check if description ends with period
    if [[ "$description" =~ \.$ ]]; then
        echo -e "${YELLOW}Warning: Description should not end with a period${NC}"
    fi
}

# Function to format commit message
format_commit_message() {
    local type="$1"
    local scope="$2"
    local description="$3"
    shift 3
    local body_points=("$@")
    
    local emoji=$(get_emoji "$type")
    
    # Create the commit subject
    local subject="${emoji} ${type}(${scope}): ${description}"
    
    # Create the commit body if body points are provided
    local body=""
    if [[ ${#body_points[@]} -gt 0 ]]; then
        body="${body}## Overview\n\n"
        body="${body}This commit ${description,,} for the ${scope} component.\n\n"
        body="${body}## Changes\n\n"
        
        for point in "${body_points[@]}"; do
            body="${body}- ${point}\n"
        done
    fi
    
    # Combine subject and body
    if [[ -n "$body" ]]; then
        echo -e "${subject}\n\n${body}"
    else
        echo "$subject"
    fi
}

# Function to show commit preview
show_preview() {
    local commit_message="$1"
    
    echo -e "${PURPLE}=== Commit Preview ===${NC}"
    echo -e "${CYAN}$commit_message${NC}"
    echo -e "${PURPLE}=====================${NC}"
    echo ""
}

# Function to confirm commit
confirm_commit() {
    echo -e "${YELLOW}Do you want to proceed with this commit? (y/N)${NC}"
    read -r response
    case "$response" in
        [yY][eE][sS]|[yY])
            return 0
            ;;
        *)
            return 1
            ;;
    esac
}

# Function to check git status
check_git_status() {
    if ! git rev-parse --git-dir > /dev/null 2>&1; then
        echo -e "${RED}Error: Not in a git repository${NC}"
        return 1
    fi
    
    # Check if there are staged changes
    if ! git diff --cached --quiet; then
        echo -e "${GREEN}Staged changes detected${NC}"
        return 0
    fi
    
    # Check if there are unstaged changes
    if ! git diff --quiet; then
        echo -e "${YELLOW}Unstaged changes detected. Staging all changes...${NC}"
        git add .
        return 0
    fi
    
    echo -e "${RED}Error: No changes to commit${NC}"
    return 1
}

# Function to show git status
show_git_status() {
    echo -e "${BLUE}=== Git Status ===${NC}"
    git status --short
    echo ""
}

# Function to perform the commit
perform_commit() {
    local commit_message="$1"
    
    echo -e "${GREEN}Committing changes...${NC}"
    
    # Use git commit with the formatted message
    if git commit -m "$commit_message"; then
        echo -e "${GREEN}✅ Commit successful!${NC}"
        
        # Show the commit details
        echo -e "${BLUE}=== Commit Details ===${NC}"
        git log --oneline -1
        echo ""
        
        # Ask if user wants to push
        echo -e "${YELLOW}Do you want to push to remote? (y/N)${NC}"
        read -r push_response
        case "$push_response" in
            [yY][eE][sS]|[yY])
                echo -e "${GREEN}Pushing to remote...${NC}"
                git push
                echo -e "${GREEN}✅ Push successful!${NC}"
                ;;
            *)
                echo -e "${YELLOW}Changes committed locally. Don't forget to push when ready.${NC}"
                ;;
        esac
    else
        echo -e "${RED}❌ Commit failed!${NC}"
        return 1
    fi
}

# Main function
main() {
    echo -e "${BLUE}🚀 Smart Commit Script${NC}"
    echo ""
    
    # Validate inputs
    validate_inputs "$@" || exit 1
    
    # Check git status
    check_git_status || exit 1
    
    # Show git status
    show_git_status
    
    # Parse arguments
    local type="$1"
    local scope="$2"
    local description="$3"
    shift 3
    local body_points=("$@")
    
    # Format commit message
    local commit_message
    commit_message=$(format_commit_message "$type" "$scope" "$description" "${body_points[@]}")
    
    # Show preview
    show_preview "$commit_message"
    
    # Confirm commit
    if confirm_commit; then
        perform_commit "$commit_message"
    else
        echo -e "${YELLOW}Commit cancelled${NC}"
        exit 0
    fi
}

# Handle help flag
if [[ "$1" == "-h" || "$1" == "--help" ]]; then
    usage
    exit 0
fi

# Run main function
main "$@"