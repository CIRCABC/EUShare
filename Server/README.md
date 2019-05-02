# EASYSHARE
Easyshare is a file exchange tool from the European Commission. Its source code is published as open source software under the terms of the EUPL public license. EUSurvey is a servlet based application and can be installed on any servlet container.

## Installation requirements
1. Tomcat 8
1. Java 8
1. MySQL 5.7
1. Maven

## Quick start

### Database initialization
1. Create eusurvey schema;
``` sql 
CREATE DATABASE easyshare; 
```

2. Create a user which will access this schema;
``` sql 
CREATE USER 'easyshare'@'localhost' IDENTIFIED BY 'easyshare'; 
GRANT ALL PRIVILEGES ON eusurveydb.* TO 'easyshare'@'localhost';
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

## Repository conventions
### Workflow
We apply the [GitFlow](https://www.atlassian.com/git/tutorials/comparing-workflows/gitflow-workflow).

### Commits
For each commit, we ask to add the number of the issue to which the commit is relevant. E.g.  Issue #1245 : Adding css for class .aClassName. 