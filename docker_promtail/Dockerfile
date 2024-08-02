FROM grafana/promtail

COPY ./promtail-config.yml /etc/promtail/promtail-config.yml

EXPOSE 9080

ENTRYPOINT ["/usr/bin/promtail", "-config.file=/etc/promtail/promtail-config.yml"]
