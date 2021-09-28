#!/bin/bash

MTA_REPORT_DIR=$GITHUB_WORKSPACE/mta-report

[ ! -d $MTA_REPORT_DIR ] && echo "Directory $MTA_REPORT_DIR DOES NOT exist" && exit -1
