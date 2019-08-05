FROM tomcat:8.5.41-jdk8

RUN ["rm", "-fr", "/usr/local/tomcat/webapps/ROOT"]
COPY ./dist/easyshareclient.war /usr/local/tomcat/webapps/ROOT.war
