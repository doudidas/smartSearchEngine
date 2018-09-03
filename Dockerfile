# Get the container image compatible with maven
FROM maven:latest

# label
LABEL author="Edouard Topin"

# Copy files on the container to the work directory
COPY . /usr/src/myapp

# Setup the work directory
WORKDIR /usr/src/myapp

# Init
RUN /usr/src/myapp/container_install.sh

#Set environnement variable

ENV CONTAINER=true

EXPOSE 9000

# run the pre-compile API
CMD ["java", "-jar", "/usr/src/myapp/API.jar","serve","/usr/src/myapp/smartSearch.yaml"]
