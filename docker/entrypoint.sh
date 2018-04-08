#!/bin/sh

java ${JAVA_OPTS} -Djava.security.egd=file:/dev/./urandom -jar /opt/app/sol-ccd-service.jar $@
