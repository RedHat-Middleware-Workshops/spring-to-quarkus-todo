#!/bin/bash

MTA_REPORT_DIR=$GITHUB_WORKSPACE/mta-report
MTA_REPORT_FILE=$MTA_REPORT_DIR/index.html

echo "Checking whether $MTA_REPORT_DIR exists"
[ ! -d $MTA_REPORT_DIR ] && echo "Directory $MTA_REPORT_DIR DOES NOT exist" && exit -1

echo "Checking whether $MTA_REPORT_FILE exists"
[ ! -s $MTA_REPORT_FILE ] && echo "File $MTA_REPORT_FILE DOES NOT exist" && exit -1

cat $MTA_REPORT_FILE

exit 0
