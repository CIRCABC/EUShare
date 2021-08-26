FROM tomcat:8.5.41-jdk8

# arguments
# ./conf/eulogin.crt is a trusted certificate for SSO integration
# ./conf/context.xml is the context configuration for tomcat
# ./conf/tomcat-users.xml is the users configuration for tomcat
# ./conf/manager-context.xml is the tomcat's manager context configuration
# ./conf/setenv.sh is the environment variables configuration

COPY ./conf/eulogin.crt eulogin.crt
RUN echo yes | keytool -import -v -alias ecas-mockup-ssl -storepass changeit -file eulogin.crt -keystore "$JAVA_HOME/jre/lib/security/cacerts"
#RUN echo yes | keytool -importcert -alias euloginCertAlias -storepass changeit -file eulogin.crt -keystore "$JAVA_HOME/jre/lib/security/cacerts"

#COPY ./conf/euloginCA.crt euloginCA.crt
#RUN echo yes | keytool -importcert -alias "eu.cec.digit.ecas.ecasmockupca (dev only)" -storepass changeit -file euloginCA.crt -keystore "$JAVA_HOME/jre/lib/security/cacerts"

RUN ["rm", "-fr", "/usr/local/tomcat/webapps/ROOT"]
COPY ./dist/easyshareserver.war /usr/local/tomcat/webapps/webservice.war
COPY ./conf/setenv.sh /usr/local/tomcat/bin/setenv.sh
COPY ./conf/context.xml /usr/local/tomcat/conf/context.xml
COPY ./conf/tomcat-users.xml /usr/local/tomcat/conf/tomcat-users.xml
COPY ./conf/manager-context.xml /usr/local/tomcat/webapps/manager/META-INF/context.xml
