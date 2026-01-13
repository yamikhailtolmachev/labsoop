FROM tomcat:9.0-jre11
LABEL maintainer="Your Name"
RUN rm -rf /usr/local/tomcat/webapps/*
COPY math-functions-api.war /usr/local/tomcat/webapps/ROOT.war
EXPOSE 8080
CMD ["catalina.sh", "run"]