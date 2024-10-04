#! /bin/bash

BRANCH_NAME=$1

# Get release versions
PLUGIN_YML=$FEATHER_CORE_ROOT/src/main/resources/plugin.yml

current_version=$(grep -oP '^version: \d+\.\d+\.\d+' $PLUGIN_YML | grep -oP '\d+\.\d+\.\d+')

IFS='.' read -r major minor patch <<<"$current_version"

if [[ "$BRANCH_NAME" == "major" ]]; then
    major=$((major + 1))
    minor=0
    patch=0
elif [[ "$BRANCH_NAME" == "feature" ]]; then
    minor=$((minor + 1))
    patch=0
else
    patch=$((patch + 1))
fi

new_version="$major.$minor.$patch"

# Update plugin.yml version
echo "Updating plugin.yml version to: $new_version"
sed -i "s/^version: .*/version: $new_version/" $PLUGIN_YML
echo "Updated plugin.yml version to: $new_version"

# Update pom.xml version
if ! command -v xmlstarlet &>/dev/null; then
    echo "xmlstarlet is not installed. Installing..."

    sudo apt-get update
    sudo apt-get install -y xmlstarlet
else
    echo "xmlstarlet is already installed."
fi

echo "Updating pom.xml version to: $new_version"
POM_XML=$FEATHER_CORE_ROOT/pom.xml
xmlstarlet ed --inplace --update "//_:project/_:version" --value "$new_version" $POM_XML
echo "Updated pom.xml version to: $new_version"

# Update release notes
echo "Updating ReleaseNotes.yml"
RELEASE_NOTES_YML=$FEATHER_CORE_ROOT/ReleaseNotes.yml

temp_file=$(mktemp)
echo $temp_file
echo "v$new_version:" >$temp_file

prev_tag="v$current_version"
git fetch --tags
git log $prev_tag..HEAD --pretty=format:"    %h: '%s'" >>$temp_file

echo "
" >>$temp_file
cat $RELEASE_NOTES_YML >>$temp_file

mv $temp_file $RELEASE_NOTES_YML

echo "Updated ReleaseNotes.yml"

# Commit, tag and push
git add $PLUGIN_YML $POM_XML $RELEASE_NOTES_YML
git commit -m "Release notes for version v$new_version"
git tag "v$new_version"
git push && git push --tag
