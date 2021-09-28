#!/bin/bash

MTA_REPORT_DIR=$GITHUB_WORKSPACE/mta-report

echo "Current dir = $(pwd)"
echo "Workspace dir = $GITHUB_WORKSPACE"
echo "ls of current dir:"
echo $(ls -al)A
echo ""

echo "Checking whether $MTA_REPORT_DIR exists"
[ ! -d $MTA_REPORT_DIR ] && echo "Directory $MTA_REPORT_DIR DOES NOT exist" && exit -1

echo "Checking whether $MTA_REPORT_DIR/index.html exists"
[ ! -s "$MTA_REPORT_DIR/index.html" ] && echo "File $MTA_REPORT_DIR/index.html DOES NOT exist" && exit -1

exit 0
