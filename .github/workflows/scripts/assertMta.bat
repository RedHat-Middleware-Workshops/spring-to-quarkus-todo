set MTA_REPORT_DIR=%GITHUB_WORKSPACE%
set MTA_REPORT_FILE=%MTA_REPORT_DIR%

echo "Checking whether %MTA_REPORT_DIR% exists"
IF NOT EXIST %MTA_REPORT_DIR% ( echo "Directory %MTA_REPORT_DIR% DOES NOT exist" && exit 7 )

echo "Checking whether %MTA_REPORT_FILE% exists"
IF NOT EXIST %MTA_REPORT_DIR% ( echo "File %MTA_REPORT_FILE% DOES NOT exist" && exit 7 )

exit 0