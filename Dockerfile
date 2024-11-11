 # renovate: datasource=github-releases depName=microsoft/ApplicationInsights-Java
ARG APP_INSIGHTS_AGENT_VERSION=3.6.2
FROM hmctspublic.azurecr.io/base/java:17-distroless
LABEL maintainer="https://github.com/hmcts/probate-back-office"

COPY build/libs/back-office.jar /opt/app/
COPY lib/applicationinsights.json /opt/app/

EXPOSE 4104 5005

COPY hosts /etc/hosts

CMD [ "back-office.jar", "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005" ]
