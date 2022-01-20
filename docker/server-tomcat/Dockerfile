FROM tomcat:8.5.41-jdk8

RUN ["rm", "-fr", "/usr/local/tomcat/webapps/ROOT"]
RUN ["rm", "-fr", "/usr/local/tomcat/webapps/examples"]
RUN ["rm", "-fr", "/usr/local/tomcat/webapps/manager"]

COPY ./conf/eulogin.crt eulogin.crt
RUN echo yes | keytool -import -v -alias ecas-mockup-ssl -storepass changeit -file eulogin.crt -keystore "$JAVA_HOME/jre/lib/security/cacerts"

COPY ./dist/eushareserver.war /usr/local/tomcat/webapps/ROOT.war

