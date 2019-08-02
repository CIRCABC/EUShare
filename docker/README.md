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
  
- Install docker-compose in that EC2 instance:
  ``` batch
    sudo curl -L "https://github.com/docker/compose/releases/download/1.24.0/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
    sudo chmod +x /usr/local/bin/docker-compose
  ``` 

## Build the Docker images
- Build the application from root folder
``` batch
mvn clean install
```
- Go into ` docker/server ` and build the angular image with `docker build . -t tomcat-easyshare-server `

## Run the environment
- Go into ` docker/ ` folder and run ` docker-compose -f docker-compose.yaml up ` 

