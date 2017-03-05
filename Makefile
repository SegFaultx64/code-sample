# SHELL := /bin/bash -e
.PHONY: run, test, fixture
.DEFAULT_GOAL := test

deps:

server: docker-compose-up

docker-compose-up: docker-compose-build
	docker-compose up -d

docker-compose-build: .built

.built:
	sbt package
	docker-compose build
	touch .built

clean: stop
	docker-compose stop
	rm -f .built
	sbt clean

stop:
	docker-compose stop
