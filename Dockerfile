ARG APP_INSIGHTS_AGENT_VERSION=2.3.1
FROM hmctspublic.azurecr.io/base/java:openjdk-8-distroless-1.0

COPY lib/applicationinsights-agent-2.3.1.jar lib/AI-Agent.xml /opt/app/

COPY build/libs/back-office.jar /opt/app/

EXPOSE 4104
CMD [ "back-office.jar" ]