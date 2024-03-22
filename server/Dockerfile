# Use an official Maven runtime as a parent image
FROM maven:3.8.4-openjdk-11

# Set the working directory in the container
WORKDIR /usr/src/app

# Copy the local project to the container
COPY pom.xml ./
COPY api.yaml ./

COPY LICENSE_HEADER.txt ./

COPY /src ./src
COPY /openapi-spring-templates ./openapi-spring-templates


# Build the application
RUN mvn clean install -P=dev -Dskip.unit.tests=true -Dskip.integration.tests=true

# Expose the port the app runs on
EXPOSE 8080

# Define the command to run your application
CMD ["java", "-jar", "target/eushareserver.jar"]
