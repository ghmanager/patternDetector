FROM openjdk:15

WORKDIR /target

RUN yum -y install libX11
RUN yum -y install mesa-libGL

COPY target/detector-0.0.1-SNAPSHOT.jar detector-0.0.1-SNAPSHOT.jar

EXPOSE 5462

CMD java -jar detector-0.0.1-SNAPSHOT.jar
