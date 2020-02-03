# Running EasyShare on Docker

## Install Docker ##
- Install docker:
  ``` batch
    sudo yum update -y
    sudo yum install -y docker
    sudo usermod -aG docker <your_user>
  ``` 
  and start docker service.
    ``` batch
    service docker start 
    ````
  
- Install docker-compose
  ``` batch
    sudo curl -L "https://github.com/docker/compose/releases/download/1.24.0/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
    sudo chmod +x /usr/local/bin/docker-compose
  ``` 

## Build the Docker images
- Build the application from root folder
``` batch
mvn clean install -Dspring.profiles.active=devdocker -Dmaven.test.skip=true
```
- Go into `docker/server` and build the tomcat image with `docker build . -t tomcat-easyshare-server`
- Go into `docker/client` and build the tomcat image with `docker build . -t tomcat-easyshare-client`

## Run the environment
- Go into ` docker/ ` folder and run ` docker-compose -f docker-compose.yaml up ` 


## Update the environment
In order to update the client and the server, you may use the tomcat7 maven plugin.
- Go into `server` and run `mvn clean tomcat7:deploy -Dmaven.test.skip=true -Dspring.profiles.active=devdocker -Dtomcat.admin=managerscript -Dtomcat.admin.password=password -Dtomcat.deploy.url=YourVMUrl/manager/text` in order to update the server with your latest changes.
- Go into `client` and run `mvn clean tomcat7:deploy -Dmaven.test.skip=true -Dspring.profiles.active=devdocker -Dtomcat.admin=managerscript -Dtomcat.admin.password=password -Dtomcat.deploy.url=YourVMUrl/manager/text` in order to update the client with your latest changes.
