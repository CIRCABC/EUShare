FROM maven:3.8.2-jdk-8

COPY ./conf/eulogin.crt eulogin.crt
RUN echo yes | keytool -import -v -alias ecas-mockup-ssl -storepass changeit -file eulogin.crt -keystore "$JAVA_HOME/jre/lib/security/cacerts"

WORKDIR /app
COPY dist/server ./

CMD ["mvn", "spring-boot:run", "-Pdev"]