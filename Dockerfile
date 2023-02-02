FROM gradle:jdk17-focal

LABEL dev.vividus.jsonpath-web.authors="team@vividus.dev"

COPY . /jsonpath/

WORKDIR /jsonpath

RUN ./gradlew :json-path-web-test:build --no-daemon

RUN printf '#!/bin/bash\njava -jar %s'  "$(find /jsonpath/json-path-web-test/build/libs/ -name '*all.jar')" > run.sh

RUN chmod +x /jsonpath/run.sh

EXPOSE 8080

HEALTHCHECK --interval=30s --timeout=2s --start-period=5s --retries=5 CMD [ "curl --fail localhost:8080" ]

ENTRYPOINT ["/jsonpath/run.sh"]
