docker run -d \
  --name mysql_container \
  -e MYSQL_ROOT_PASSWORD=root \
  -v ~/mysql_data:/var/lib/mysql \
  -p 3306:3306 \
  --network host \
  mysql:latest
