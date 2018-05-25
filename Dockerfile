FROM openjdk:8-jre-alpine

USER root

WORKDIR /home/root

COPY target/gold-price-collector-*.jar gold-price-collector.jar

RUN mkdir wrkdir

VOLUME /home/root/wrkdir

ENV url_password XXX
ENV environment AWS
ENV uptime 770

CMD java -Denvironment=${environment} -jar gold-price-collector.jar ${url_password} ${uptime}