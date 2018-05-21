FROM openjdk:8-jre-alpine

USER root

WORKDIR /home/root

COPY target/gold-price-collector-*.jar gold-price-collector.jar

RUN mkdir wrkdir

VOLUME /home/root/wrkdir

ENV url_password XXX

ENV event_store_address 127.0.0.1

CMD java -jar gold-price-collector.jar ${url_password} ${event_store_address}