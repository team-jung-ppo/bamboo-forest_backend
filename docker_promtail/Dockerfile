FROM grafana/promtail

RUN groupadd --system --gid 1001 promtail && \
    useradd --system --uid 1001 --gid promtail --no-create-home --shell /bin/false promtail

COPY ./promtail-config.yml /etc/promtail/promtail-config.yml

RUN chown promtail:promtail /etc/promtail/promtail-config.yml

USER promtail:promtail

EXPOSE 9080

ENTRYPOINT ["/usr/bin/promtail", "-config.file=/etc/promtail/promtail-config.yml"]
