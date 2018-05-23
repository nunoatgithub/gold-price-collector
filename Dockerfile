FROM openjdk:8-jre-alpine

USER root

WORKDIR /home/root

COPY target/gold-price-collector-*.jar gold-price-collector.jar
COPY history history

RUN mkdir wrkdir

VOLUME /home/root/wrkdir

ENV url_password XXX

CMD java -jar gold-price-collector.jar ${url_password}