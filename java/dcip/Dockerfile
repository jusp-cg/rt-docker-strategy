FROM cgprdregistry.capgroup.com/cgbuild/deveng/cgjdk11:latest
USER root
COPY . /tmp/app-build
WORKDIR /tmp/app-build

RUN \
chown 0:root /tmp/app-build && \
mvn clean package -Dmaven.test.skip=true && \
mkdir -p /deployments/app && \
mkdir -p /deployments/tools/app-dynamics/ApplicationAgent/logs && \
chown 0:root -R /deployments && \
chmod -R 775 /deployments && \
cp /tmp/app-build/dcip-main/target/dcip-main-0.0.1-SNAPSHOT.jar /deployments/app && \
rm -rf /tmp/app-build

COPY app-dynamics /deployments/tools/app-dynamics/ApplicationAgent
RUN chmod -R 775 /deployments
COPY docker-entrypoint.sh /docker-entrypoint.sh
RUN chmod +x /docker-entrypoint.sh

USER 10001
RUN mkdir /cg-artifacts
RUN mkdir /cg-artifacts/test
EXPOSE 8080
ENTRYPOINT ["/docker-entrypoint.sh"]