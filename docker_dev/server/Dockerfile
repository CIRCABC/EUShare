FROM openjdk:11

COPY ./conf/eulogin.crt ./eulogin.crt

RUN keytool -importcert -file ./eulogin.crt -alias cas-mockup-ssl -cacerts -storepass changeit -noprompt

WORKDIR /app

COPY bin/.mvn/ .mvn
COPY bin/mvnw mvnw
COPY conf/pom.xml ./
#RUN ./mvnw dependency:go-offline
# create volume for src and target
#COPY src ./src



CMD ["./mvnw", "spring-boot:run"]