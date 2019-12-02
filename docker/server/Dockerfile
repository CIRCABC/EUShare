FROM tomcat:8.5.41-jdk8

COPY ./conf/eulogin.crt eulogin.crt
RUN echo yes | keytool -importcert -alias euloginCertAlias -storepass changeit -file eulogin.crt -keystore "/docker-java-home/jre/lib/security/cacerts"

RUN ["rm", "-fr", "/usr/local/tomcat/webapps/ROOT"]
COPY ./lib/mysql-connector-java-8.0.16.jar /usr/local/tomcat/lib
COPY ./dist/easyshareserver.war /usr/local/tomcat/webapps/ROOT.war
COPY ./conf/setenv.sh /usr/local/tomcat/bin/setenv.sh
COPY ./conf/context.xml /usr/local/tomcat/conf/context.xml
COPY ./conf/tomcat-users.xml /usr/local/tomcat/conf/tomcat-users.xml
COPY ./conf/manager-context.xml /usr/local/tomcat/webapps/manager/META-INF/context.xml
COPY wait-for-it.sh /usr/local/tomcat/wait-for-it.sh
RUN ["chmod", "+x", "/usr/local/tomcat/wait-for-it.sh"]
CMD ["/usr/local/tomcat/wait-for-it.sh","db:3306","--timeout=0","--strict", "--","catalina.sh", "run"]