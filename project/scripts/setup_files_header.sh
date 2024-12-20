#!/bin/bash

# » env variables
source $FEATHER_CORE_ROOT/project/scripts/env.sh

# » helpers
function header_print() {
    print "${DARK_GRAY}[${DARK_GREEN}Headers${DARK_GRAY}]${RESET} » ${LIGHT_GRAY}$1"
}

header_print "Begin"

# Loop through all .java files
find . -type f -name "*.java" | while read -r file; do
    # Check if the file already contains the header
    if ! grep -q "/\\*\\*" "$file" && ! grep -q "// testfile" "$file"; then
        # Get the filename without the path
        filename=$(basename "$file")

        if [[ $filename == *"Test.java" ]]; then
            # Define the test header
            header="/**
 * ------------------------------------------------------------------------- *
 *                     Copyright (c) by FeatherCore 2024                     *
 * ------------------------------------------------------------------------- *
 * @license https://github.com/TheAncientOwl/feather-core/blob/main/LICENSE
 *
 * @file $filename
 * @author Alexandru Delegeanu
 * @version 0.1
 * @test_unit ${filename:0:-9}#version
 * @description Unit tests for ${filename:0:-9}
 */
 "
        else
            # Define the general header
            header="/**
 * ------------------------------------------------------------------------- *
 *                     Copyright (c) by FeatherCore 2024                     *
 * ------------------------------------------------------------------------- *
 * @license https://github.com/TheAncientOwl/feather-core/blob/main/LICENSE
 *
 * @file $filename
 * @author Alexandru Delegeanu
 * @version 0.1
 * @description TODO: add description
 */"
        fi

        header_print "Adding header to $file"
        (
            echo "$header"
            cat "$file"
        ) >temp_file && mv temp_file "$file"
    fi
done

header_print "Done"
