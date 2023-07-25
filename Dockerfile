FROM clojure:temurin-20-bullseye AS clj

WORKDIR /app
COPY deps.edn /app/deps.edn
RUN clojure -P
