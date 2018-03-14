FROM openjdk:8-jre-alpine

USER root

WORKDIR /home/root

COPY gold-price-collector-*.jar gold-price-collector.jar

RUN mkdir wrkdir

COPY upgrade-to-latest.sh /home/root/wrkdir/upgrade-to-latest.sh

VOLUME /home/root/wrkdir

ENV url_password XXX

CMD java -jar gold-price-collector.jar ${url_password}