FROM gradle:6.9.1-jdk11-hotspot as BUILD_IMAGE
COPY --chown=gradle:gradle . /home/hermes/
WORKDIR /home/hermes
RUN gradle :bootJar

FROM openjdk:11.0.12-jre
WORKDIR /home/hermes
COPY --from=BUILD_IMAGE /home/hermes/build/libs/hermes-*.jar app.jar
EXPOSE 8080
ENV JAVA_OPTS=""
CMD java $JAVA_OPTS -jar app.jar
