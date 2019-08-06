FROM tomcat:8.5.41-jdk8

RUN ["rm", "-fr", "/usr/local/tomcat/webapps/ROOT"]
COPY ./conf/tomcat-users.xml /usr/local/tomcat/conf/tomcat-users.xml
COPY ./conf/manager-context.xml /usr/local/tomcat/webapps/manager/META-INF/context.xml
COPY ./dist/easyshareclient.war /usr/local/tomcat/webapps/ROOT.war
