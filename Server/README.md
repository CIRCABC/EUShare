# EASYSHARE
Easyshare is a file exchange tool from the European Commission. Its source code is published as open source software under the terms of the EUPL public license. 

* [Latest Sonar Cloud analysis](https://sonarcloud.io/dashboard?id=CIRCABC_EasyShare) ![Bugs](https://sonarcloud.io/api/project_badges/measure?project=CIRCABC_EasyShare&metric=bugs) ![Code smells](https://sonarcloud.io/api/project_badges/measure?project=CIRCABC_EasyShare&metric=code_smells) ![Coverage](https://sonarcloud.io/api/project_badges/measure?project=CIRCABC_EasyShare&metric=coverage)

## Installation requirements
1. Tomcat 8
1. Java 8
1. MySQL 5.7
1. Maven

## Quick start

### Database initialization
1. Create easyshare schema;
``` sql 
CREATE DATABASE easyshare; 
```

2. Create a user which will access this schema;
``` sql 
CREATE USER 'easyshare'@'localhost' IDENTIFIED BY 'easyshare'; 
GRANT ALL PRIVILEGES ON easyshare.* TO 'easyshare'@'localhost';
```

3. Set event-scheduler to ON.
``` sql 
SET GLOBAL event_scheduler = ON
```

### Run the application
Build the application's jar using the following command:
``` batch
mvn clean install
```

Build the test report by running the following command:
``` batch
mvn surefire-report:report
```

Run the application by running the following command:
``` batch
java -jar target\<applicationName>.jar
```

Run the application in debug mode by running the following command:
``` batch
java -jar target\<applicationName>.jar -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=8000
```

## Repository conventions
### Workflow
We apply the [GitFlow](https://www.atlassian.com/git/tutorials/comparing-workflows/gitflow-workflow).

### Commits
For each commit, we ask to add the number of the issue to which the commit is relevant. E.g.  Issue #1245 : Adding css for class .aClassName. 