# Get the container image compatible with maven
FROM maven:latest as build

# Copy files on the container to the work directory
COPY . /usr/src/myapp

# Setup the work directory
WORKDIR /usr/src/myapp

# Init
RUN /usr/src/myapp/container_install.sh

FROM java:alpine
RUN mkdir /app 
COPY  --from=build  /usr/src/myapp/ /app
WORKDIR /app 

#Set environnement variable

ENV CONTAINER=true

EXPOSE 9000

# label
LABEL author="Edouard Topin"

# run the pre-compile API
CMD ["java", "-jar", "/app/API.jar","serve","/app/smartSearch.yaml"]
