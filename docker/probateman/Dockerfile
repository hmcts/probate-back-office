FROM postgres:9.6

COPY init.sh /docker-entrypoint-initdb.d

COPY . /setUp
WORKDIR /setUp
RUN init.sh

EXPOSE 5432
