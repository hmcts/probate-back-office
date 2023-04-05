ARG APP_INSIGHTS_AGENT_VERSION=3.4.11
FROM hmctspublic.azurecr.io/base/java:11-distroless
LABEL maintainer="https://github.com/hmcts/probate-back-office"

COPY lib/AI-Agent.xml /opt/app/
COPY build/libs/back-office.jar /opt/app/
COPY lib/applicationinsights.json /opt/app/

EXPOSE 4104

CMD [ "back-office.jar" ]
