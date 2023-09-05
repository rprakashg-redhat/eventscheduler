FROM registry.access.redhat.com/ubi8/openjdk-11-runtime

RUN mkdir /home/jboss/eventscheduler

WORKDIR /home/jboss/eventscheduler

COPY target/quarkus-app/ .

EXPOSE 8080

ENTRYPOINT [ "java", "-jar", "quarkus-run.jar" ]