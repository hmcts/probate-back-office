#!/bin/sh

java ${JAVA_OPTS} -Djava.security.egd=file:/dev/./urandom -jar /opt/app/bo-sol-ccd-service.jar $@
