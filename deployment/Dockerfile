# Base Image
FROM tomcat:9.0-jdk8

ARG TAG
ENV LAREX_VERSION=$TAG

# Enable Networking on port 8080 (Tomcat)
EXPOSE 8080

# Installing dependencies and deleting cache
RUN apt-get update && apt-get install -y

# Download maven project
COPY Larex.war /usr/local/tomcat/webapps/Larex.war

# Create books and savedir path
RUN mkdir /home/books /home/savedir

# Copy larex.properties
ENV LAREX_CONFIG=/larex.properties
