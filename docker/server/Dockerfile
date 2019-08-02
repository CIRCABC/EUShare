FROM tomcat:8.5.41-jdk8

RUN ["rm", "-fr", "/usr/local/tomcat/webapps/ROOT"]
COPY ./lib/mysql-connector-java-8.0.16.jar /usr/local/tomcat/lib
COPY ./dist/easyshareserver.war /usr/local/tomcat/webapps/ROOT.war
COPY ./conf/context.xml /usr/local/tomcat/conf/context.xml
COPY wait-for-it.sh /usr/local/tomcat/wait-for-it.sh

CMD ["/usr/local/tomcat/wait-for-it.sh","db:3306","--timeout=0","--strict", "--","catalina.sh", "run"]