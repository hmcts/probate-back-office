ARG APP_INSIGHTS_AGENT_VERSION=2.3.1
FROM hmctspublic.azurecr.io/base/java:openjdk-8-distroless-1.0
# Mandatory!
ENV APP back-office.jar

COPY build/libs/$APP /opt/app/
COPY lib/applicationinsights-agent-2.3.1.jar lib/AI-Agent.xml /opt/app/

EXPOSE 4104

CMD [ "back-office.jar" ]
