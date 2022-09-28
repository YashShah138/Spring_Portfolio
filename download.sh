#!/bin/bash

sudo apt update; sudo apt upgrade
sudo apt install default-jre
java --version
sudo apt install default-jdk
javac --version
sudo apt update
sudo apt upgrade
sudo apt install maven
mvn --version
cd ~
git clone https://github.com/guapbeast/Spring.git
cd ~/Spring
./mvnw package
RESULT=$?
if [[ $RESULT -eq 0 ]]
then
  cd ~/Spring
else
  sudo apt install -y dos2unix
  dos2unix ./mvnw
  chmod +x ./mvnw
fi

echo '
# syntax=docker/dockerfile:1
FROM openjdk:16-alpine3.13
WORKDIR /app
RUN apk update && apk upgrade && \
  apk add --no-cache git maven
RUN git clone https://github.com/guapbeast/Spring.git /app
RUN ./mvnw package
CMD ["java", "-jar", "target/spring-0.0.1-SNAPSHOT.jar"]
# EXPOSE port that is defined in spring_portfolio.git application.properties
EXPOSE 8085' > Dockerfile

echo '
version: "3"
services:
  web:
    image: java_springv1
    build: .
    ports:
      - "8085:8085"
    volumes:
      - persistent_volume:/app/volumes
volumes:
  persistent_volume:
    driver: local
    driver_opts:
      o: bind
      type: none
      device: /home/ubuntu/Spring/volumes' > docker-compose.yml

sudo apt install docker-compose -y
sudo docker-compose up -d
sudo apt install nginx
cd /etc/nginx/sites-available
sudo touch Spring

echo '
server {
    listen 80;
    listen [::]:80;
    server_name csarithwikh.live www.csarithwikh.live;

    location / {
        proxy_pass http://localhost:8085;
        # Simple requests
        if ($request_method ~* "(GET|POST)") {
                add_header "Access-Control-Allow-Origin"  *;
        }

        # Preflight requests
        if ($request_method = OPTIONS ) {
                add_header "Access-Control-Allow-Origin"  *;
                add_header "Access-Control-Allow-Methods" "GET, POST, OPTIONS, HEAD";
                add_header "Access-Control-Allow-Headers" "Authorization, Origin, X-Requested-With, Content-Type, Accept";
                return 200;
        }
    }
}' > Spring

sudo ln -s /etc/nginx/sites-available/Spring /etc/nginx/sites-enabled
sudo nginx -t

RESULT=$?
if [[ $RESULT -eq 0 ]]
then
  sudo systemctl restart nginx
else
  echo "Something went wrong!!!"
fi

curl http://localhost:8085;

sudo snap install core; sudo snap refresh core
sudo snap install --classic certbot
sudo ln -s /snap/bin/certbot /usr/bin/certbot
sudo certbot --nginx