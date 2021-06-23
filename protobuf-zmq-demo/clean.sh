#!/bin/bash
# iterate over child folders two levels deep and execute the clean.sh script, if it exists
find . -name '*.classpath' -or -name '.project' -or -name ".settings" -or -name "target" -or -name ".idea" -or -name "*.iml"| xargs rm -rf

