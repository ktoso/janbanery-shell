#!/bin/sh
cd `dirname "$0"`
mvn -DskipTests clean package
java -jar target/uber-janbanery-shell-1.0-SNAPSHOT.jar 
