#!/bin/bash
exec java \
  -XX:MaxRAMPercentage=95 \
  -javaagent:/deployments/tools/app-dynamics/ApplicationAgent/javaagent.jar \
  -jar /deployments/app/dcip-main-0.0.1-SNAPSHOT.jar --spring.config.location=file:/deployments/config/application_oc_release.properties \
  -Dappdynamics.agent.reuse.nodeName=${APPDYNAMICS_AGENT_NODE_REUSE} \
  -Dappdynamics.jvm.shutdown.mark.node.as.historical=${APPDYNAMICS_NODE_SHUTDOWN_HISTORICAL} \
  -Dappdynamics.agent.reuse.nodeName.prefix=${APPDYNAMICS_AGENT_REUSE_NODE_NAME_PREFIX} \
  -Dappdynamics.agent.logs.dir=/deployments/tools/app-dynamics/ApplicationAgent/javaagent/logs \
  -Dappdynamics.agent.applicationName=DCIP \
  -Dappdynamics.agent.tierName=Tomcat \
  -XX:+PrintFlagsFinal -version
