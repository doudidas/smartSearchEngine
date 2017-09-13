# Get the container image compatible with maven
FROM maven:latest

# Copy files on the container to the work directory
COPY . /usr/src/myapp

# Setup the work directory
WORKDIR /usr/src/myapp

# Creation of the jar file
RUN mvn clean package

# Remove the jar
RUN mv ./target/API.jar ./API.jar

# Clean directory
RUN mvn clean


###
# Mongodb setup
#RUN apt-get update && apt-get -y install mongodb
#
#
#RUN mkdir /data
#RUN mkdir /data/db
#RUN mongod &
#
#EXPOSE 27017
#
###

EXPOSE 9000

# run the pre-compile API
CMD ["java", "-jar", "./API.jar","serve","src/main/resources/smartSearch.yaml"]
