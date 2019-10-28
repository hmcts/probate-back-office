ARG APP_INSIGHTS_AGENT_VERSION=2.5.1-BETA
FROM hmctspublic.azurecr.io/base/java:openjdk-8-distroless-1.2
LABEL maintainer="https://github.com/hmcts/probate-back-office"
COPY lib/AI-Agent.xml /opt/app/
COPY lib/applicationinsights-agent-2.5.1-BETA.jar /opt/app/

COPY build/libs/back-office.jar /opt/app/

EXPOSE 4104
CMD [ "back-office.jar" ]
