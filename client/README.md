# EUSHARE
EUShare is a file exchange tool from the European Commission. Its source code is published as open source software under the terms of the EUPL public license.

* [Latest Sonar Cloud analysis](https://sonarcloud.io/dashboard?id=EASYSHARE_FRONT) ![Bugs](https://sonarcloud.io/api/project_badges/measure?project=EASYSHARE_FRONT&metric=bugs) ![Code smells](https://sonarcloud.io/api/project_badges/measure?project=EASYSHARE_FRONT&metric=code_smells) ![Coverage](https://sonarcloud.io/api/project_badges/measure?project=EASYSHARE_FRONT&metric=coverage)

## Installation requirements
1. NPM 6.10.2
1. Node 10.14.1
1. Typescript 3.2.2
1. Angular 7.1.4

or 
1. Tomcat 8
1. Java 8
1. MySQL 5.7
1. Maven 3.6.0

## Quick start
### Run the application
Build & run the application by using the following command:
``` batch
# from angular folder
npm install
npm run start
```

Or build a war containing the angular application from root folder
``` batch
# from client folder
mvn clean install
```

and upload it to your running tomcat (after setting up a manager-script user in tomcat)
``` batch
# from client folder
mvn clean tomcat7:deploy 
-Dtomcat.admin=your_tomcat_username
-Dtomcat.admin.password=your_tomcat_password
-Dtomcat.deploy.url=your_tomcat_url
```

## Repository conventions
### Workflow
We apply the [GitFlow](https://www.atlassian.com/git/tutorials/comparing-workflows/gitflow-workflow).

### Commits
For each commit, we ask to add the number of the issue to which the commit is relevant. E.g.  Issue #1245 : Adding css for class .aClassName. For each branch, we ask it to be named by the issue name then its number. E.g. for an issue like named "Dockerize environment" numbered 1246, the branch name will be "dockerizeEnvironment#1246".