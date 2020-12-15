FROM openjdk:8-jre-alpine

## New Relic
RUN apk add curl
RUN mkdir /opt/newrelic
RUN curl -O "http://download.newrelic.com/newrelic/java-agent/newrelic-agent/current/newrelic-java.zip"
RUN unzip newrelic-java.zip -d /opt
 
ENV aws.s3.bucketname odp-us-innovation-daas
ENV aws.dynamodb.profile odp-innovation-daas
ENV CLASSPATH /opt/lib
COPY pom.xml target/lib* /opt/lib/
COPY target/*.jar /opt/app.jar
WORKDIR /opt



EXPOSE 8090
