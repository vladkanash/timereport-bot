FROM openjdk:11-jre
EXPOSE 3000
ADD timereport-bot-0.1.0.tar .
ENTRYPOINT ["/timereport-bot-0.1.0/bin/timereport-bot"]
