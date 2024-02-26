#!/bin/bash

echo "Setting up environment..."
# shellcheck disable=SC1090
source <(awk '/^[[:space:]]*[^#]/ && /^[[:space:]]*[^[:space:]#=]+=[^[:space:]]+/{print "export " $0}' env.properties)