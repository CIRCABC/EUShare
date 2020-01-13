mvn -f pom_jib.xml clean compile jib:dockerBuild -Dimage=server -Dspring.profiles.active=docker -Dmaven.test.skip=true -e -X
# docker run -p 8080:80 server
