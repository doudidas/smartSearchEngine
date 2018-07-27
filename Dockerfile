# Get the container image compatible with maven
FROM maven:latest

# label
LABEL author="Edouard Topin"

# Copy files on the container to the work directory
COPY . /usr/src/myapp

# Setup the work directory
WORKDIR /usr/src/myapp

RUN mvn package;
# Init
RUN /usr/src/myapp/container_install.sh

#Set environnement variable

ENV CONTAINER=true

EXPOSE 9000

# run the pre-compile API
CMD ["java", "-jar", "./API.jar","serve","./smartSearch.yaml"]
