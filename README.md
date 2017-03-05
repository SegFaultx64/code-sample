# Analytics #

## Requirements ##

* Docker
* Docker Compose
* JDK 8
* Make

## Build & Run ##

`make clean server`

That might need a `sudo` in front on ubuntu depending on how your docker is installed.

##  Make Request ##

Add data

`curl -XPOST http://localhost:8686/analytics\?timestamp\=1480748536000\&user\=3434dsesd\&event\=click` 

Get data

`curl -XGET http://localhost:8686/analytics\?timestamp\=1480748536000`

Additionally the app is alread running at: `http://pleasegivemajob.maxwalker.me:8686` so you can use that if you don't want to setup docker.

