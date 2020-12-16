#!/bin/bash
#
# Deploy one module (provided as .war file) to tomcat, with backup and restore if needed.
#
# Prerequsites
#	$CATALINA_BASE/ is set (the Tomcat installation directory)
#	$CATALINA_BASE/webapps/ is writeable
# Parameter(s)
#	the .war file on target machine (users $HOME or absolute path)
# Diagnostics, jenkins job tokens
#	"FAILED" if deployment failed for any reason (restore previous state from backup)
#	"SUCCESS" if deployment done; test application responsibility
#	"UNSTABLE" if deployment done but does not respond well
# Copyright 2017, GE Healthcare
# v.0.99.4

prog=`basename $0`
if [ -z "$1" ]; then
	echo "Usage: $prog <warfile>"
	exit 1
fi

#---

TIMESTAMP=`date "+%Y-%m-%d_%H-%M-%S"`
TIMEOUT=240

abort() {
	exitval=$1
	shift
	echo "*** FAILED..."
	for msg in "$@" ; do
		echo "=> $msg"
	done
	exit $exitval
}

wait_until_folder_disappear() {
	TIMER=0
	while [ -d $CATALINA_BASE/webapps/$app ] && [ $TIMER -lt $TIMEOUT ]; do
		TIMER=$((TIMER+1))
		echo -n " $TIMER"
		sleep 1
	done
	echo ""
}

checking_folder() {
	TIMER=0
	while [ ! -d $CATALINA_BASE/webapps/$app ] && [ $TIMER -lt $TIMEOUT ]; do
		TIMER=$((TIMER+1))
		echo -n " $TIMER"
		sleep 1
	done
	echo ""
}

test_tomcat() {
	TEST_URL="http://localhost:8080"
	echo "Checking Tomcat... $TEST_URL"
	curl -s $TEST_URL
	if [ $? -eq 0 ]; then
		echo "Tomcat is listening."
		return 0
	else
		echo "No response."
		return 1
	fi
}

test_tomcat_finish() {
	TEST_URL="http://localhost:8080/$app"
	echo "=== Checking Tomcat... $TEST_URL"
	curl -s -I $TEST_URL | head -n 1 | egrep "(200|302)"
	if [ $? -eq 0 ]; then
		echo "$app app is running. SUCCESS"
	else
		curl -s -I $TEST_URL | head -n 1
		echo "$app app is not running... build UNSTABLE"
		# continue
	fi
	TEST_URL="http://localhost:8080/$app/ack.jsp"
	echo "=== Checking Tomcat... $TEST_URL"
	curl -s -I $TEST_URL | head -n 1 | egrep "(200|302)"
	if [ $? -eq 0 ]; then
		echo "$app app is running. SUCCESS"
		curl -s $TEST_URL
		# get the version (only FX)
		curl -s http://localhost:8080/$app/version.html
	else
		curl -I $TEST_URL 2>/dev/null | head -n 1
		echo "$app app is not running... build FAILED"
		exit 1
	fi
}

echo "=== Checking Prerequsites"
df -Th -x tmpfs
if [ -z "$CATALINA_BASE" ]; then
	abort 9 "CATALINA_BASE is not set."
fi
[ -f "$1" ] || abort 9 "Input file $1 not found"
warfile=$(basename $1)
FILEDIR=$(cd $(dirname $1); pwd)
testoption="$2"   # more tests for FX like apps
TOMCAT_PID=`ps afx | egrep '/tomcat' | egrep -v "(grep|awk|$prog)" | awk '{print $1}'`
if [ -z "$TOMCAT_PID" ]; then
	abort 9 "Tomcat is not running."
fi
echo "Tomcat is up and running, `echo $TOMCAT_PID`"
echo "md5sum of file to deploy"
md5sum $FILEDIR/$warfile

echo "=== Checking directories"
touch $CATALINA_BASE/webapps/.test_$TIMESTAMP || abort 8 "$CATALINA_BASE/webapps/ is not writeable"
rm $CATALINA_BASE/webapps/.test_$TIMESTAMP
BACKUP_DIR="$CATALINA_BASE/backup"
if [ ! -d $BACKUP_DIR ]; then
	mkdir $BACKUP_DIR || BACKUP_DIR="$HOME/webapps_backup"
	[ -d $BACKUP_DIR ] || mkdir $BACKUP_DIR
fi
touch $BACKUP_DIR/.test_$TIMESTAMP || abort 8 "$BACKUP_DIR/ is not writeable"
rm $BACKUP_DIR/.test_$TIMESTAMP
echo ok

app=$(basename $warfile .war)
if [ -f $CATALINA_BASE/webapps/$warfile ]; then
	[ -d $CATALINA_BASE/webapps/$app ] || abort 7 "$warfile file exists but $app folder doesn't. Not deploying..."
else
	[ -d $CATALINA_BASE/webapps/$app ] && abort 6 "$app folder exists without $warfile file. Not deploying..."
fi

if [ ! -f $CATALINA_BASE/webapps/$warfile ]; then
	echo "=== First deployment, no backup ..."

else
	echo "=== Remove old version of $app (backup) ..."
	mv -v $CATALINA_BASE/webapps/$warfile $BACKUP_DIR/$warfile.$TIMESTAMP.bkp || abort 7 "backup failed"

	echo "Waiting for $app directory to disappear ..."
	wait_until_folder_disappear

	if [ -d $CATALINA_BASE/webapps/$app ]; then
		echo "$app folder still exists, abort deployment, start restore ..."
		mv -v $BACKUP_DIR/$warfile.$TIMESTAMP.bkp $CATALINA_BASE/webapps/$warfile || echo "restore failed"
		abort 6 "$app folder not removed by Tomcat"
	fi
	# backup done
fi

echo "=== Deploy new version of $app ..."
mv -v $FILEDIR/$warfile $CATALINA_BASE/webapps/$warfile || abort 5 "Failed to move $warfile to $CATALINA_BASE/webapps/"
chmod 644 $CATALINA_BASE/webapps/$warfile || echo "chmod failed"

echo "Checking $app directory ..."
checking_folder

if [ ! -d $CATALINA_BASE/webapps/$app ]; then
	echo "=== $app folder does not exist (deployment failed)"
	test_tomcat

	# restore
	if [ -f "$BACKUP_DIR/$warfile.$TIMESTAMP.bkp" ]; then
		echo "Restore old $warfile from backup ..."
		mv -v $BACKUP_DIR/$warfile.$TIMESTAMP.bkp $CATALINA_BASE/webapps/$warfile || echo "restore failed"

		echo "Checking $app directory ..."
		checking_folder

		if [ ! -d $CATALINA_BASE/webapps/$app ]; then
			echo "$app folder does not exist (restore failed)"
			test_tomcat
			abort 4 "$app folder not created by Tomcat" "Restore: $app folder does not exist"
		else
			abort 3 "$app folder not created by Tomcat" "Restore: ok, $app folder exist"
			# functionality not checked
		fi
	else
		echo "Nothing to restore, delete deployed $warfile ..."
		rm $CATALINA_BASE/webapps/$warfile || echo "remove failed"

		abort 3 "$app folder not created by Tomcat" "Nothing to restore"
	fi
fi

echo "=== Deployment SUCCESSFUL"
sleep 10  # wait at least 3 seconds, otherwise TEST_URL returns 404
rm -f $BACKUP_DIR/$warfile.*.bkp 2>/dev/null

echo "=== Response tests"
test_tomcat
[ "$testoption" == "FX" ] && ( echo "" ; sleep 10 ; test_tomcat_finish )
echo
echo "==="
exit 0
