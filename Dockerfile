# Get the container image compatible with maven
FROM maven:latest

# label
LABEL author="Edouard Topin"

# Copy files on the container to the work directory
COPY . /usr/src/myapp

# Setup the work directory
WORKDIR /usr/src/myapp

# Creation of the jar file
RUN mvn clean package

# Move the jar
RUN mv ./target/API.jar ./API.jar

# Get the yaml file
RUN mv src/main/resources/smartSearch.yaml .

# Clean directory
RUN mvn clean

# Clean other files
RUN rm -rf DockerFile README.md	pom.xml src

EXPOSE 9000



# run the pre-compile API
CMD ["java", "-jar", "./API.jar","serve","./smartSearch.yaml"]
