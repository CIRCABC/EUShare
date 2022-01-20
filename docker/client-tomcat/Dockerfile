FROM tomcat:8.5.41-jdk8

RUN ["rm", "-fr", "/usr/local/tomcat/webapps/ROOT"]
RUN ["rm", "-fr", "/usr/local/tomcat/webapps/examples"]
RUN ["rm", "-fr", "/usr/local/tomcat/webapps/manager"]

COPY ./dist/eushareclient.war /usr/local/tomcat/webapps/ROOT.war


