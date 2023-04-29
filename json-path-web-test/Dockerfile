FROM eclipse-temurin:11-jre-jammy

ARG VERSION
ARG SLF4J_VERSION=1.7.36

WORKDIR /opt/app
EXPOSE 8080

ADD json-path-web-test-${VERSION}.tar /opt/app/
RUN mv json-path-web-test-${VERSION} json-path-web-test
ADD https://repo1.maven.org/maven2/org/slf4j/slf4j-simple/${SLF4J_VERSION}/slf4j-simple-${SLF4J_VERSION}.jar /opt/classpath/

CMD ["java", "-classpath", "/opt/app/json-path-web-test/lib/*:/opt/classpath/*", "com.jayway.jsonpath.web.boot.Main"]
