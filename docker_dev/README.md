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

## Run the environment
- Go into ` docker_dev/ ` folder and run ` build.sh ` 

## Client image
- Client is built using Multi Stage Build
- For more info check:
- <https://dev.to/avatsaev/create-efficient-angular-docker-images-with-multi-stage-builds-1f3n>

## Server image
- Server image is built using jib maven plugin
- For more info check :
- <https://github.com/GoogleContainerTools/jib>