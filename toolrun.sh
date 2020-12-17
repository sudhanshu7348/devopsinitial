#!/bin/bash
set -x

COVERITY="/opt/cov-analysis-linux64-2018.06"
COV_HOST="coverity.cloud.health.ge.com"
COV_PORT=8080

INTER_DIR="cov-intermediate"
STREAM="Fx-Frontend Stream"
export M2_HOME=/var/lib/jenkins/tools/hudson.tasks.Maven_MavenInstallation/Maven_3.3.9

echo "#"
echo "# COVERITY [$COVERITY]"
echo "# STREAM [$STREAM]"
which java
java -version
echo "#"

cat > "build.sh" <<EOL
#!/bin/bash
export PATH=$M2_HOME/bin:$PATH

cd WebService/
mvn clean install -Dmaven.test.skip=true -Denv.name=brk
cd ..

EOL
chmod +x build.sh

echo "#"
echo "# cov-build ..."
echo "#"

$COVERITY/bin/cov-build --dir $INTER_DIR \
  --delete-stale-tus \
  ./build.sh
COV_BUILD=$?

if test $COV_BUILD -gt 0; then
  cat "cov-intermediate/output/summary.txt"
  exit $COV_BUILD
fi

echo "#"
echo "# cov-analyze ..."
echo "#"

$COVERITY/bin/cov-analyze --dir $INTER_DIR \
  --all \
  --jobs auto \
  --java \
  --webapp-security \
  --skip-webapp-sanity-check \
  --distrust-all \
  --paths 220000 \
  --strip-path "${WORKSPACE}" \
  --preview
COV_ANALYZE_JAVA=$?

if test $COV_ANALYZE_JAVA -gt 0; then
  cat "cov-intermediate/output/summary.txt"
  exit $COV_ANALYZE_JAVA
fi

echo "#"
echo "# cov-commit-defects ..."
echo "#"

$COVERITY/bin/cov-commit-defects --dir $INTER_DIR \
  --auth-key-file ${COV_AUTH_KEY_FILE} \
  --host ${COV_HOST} --port ${COV_PORT} \
  --stream "${STREAM}"
COV_COMMIT_DEFECTS=$?

if test $COV_COMMIT_DEFECTS -gt 0; then
  cat "cov-intermediate/output/summary.txt"
  exit $COV_COMMIT_DEFECTS
fi

echo "done"
