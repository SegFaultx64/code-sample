FROM jetty:9.4.2-alpine
ADD ./target/scala-2.12/*.war /var/lib/jetty/webapps/ROOT.war