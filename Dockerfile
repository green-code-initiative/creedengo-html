FROM maven:3-openjdk-11-slim AS builder

COPY . /usr/src/creedengo

WORKDIR /usr/src/creedengo/tools
RUN ./build.sh

FROM sonarqube:10-community
COPY --from=builder /usr/src/creedengo/target/creedengo-*.jar /opt/sonarqube/extensions/plugins/
