package website.pleasegivemeajob.analytics

import redis.clients.jedis.JedisPool
import redis.clients.jedis.JedisPoolConfig

case class AnalyticsDatapoint(timestamp: Long, user: String, event: Event) {

  def writeToRedis(): Unit = {
    val HOUR_IN_MILLIS = 3600000;

    AnalyticsDatapoint.maybeConnection.map(connection => {
      val jedisInst = connection.getResource();
      jedisInst.zadd(event.getName, timestamp.toDouble, user + timestamp)
      jedisInst.zadd("users", timestamp.toDouble, user + (Math.floor(timestamp / HOUR_IN_MILLIS) * HOUR_IN_MILLIS))
      jedisInst.close();
    })
  }

}

object AnalyticsDatapoint {
  implicit val maybeConnection: Option[JedisPool] = {
    val poolConfig: JedisPoolConfig = new JedisPoolConfig();
    poolConfig.setMaxTotal(1024);
    poolConfig.setMaxIdle(512);
    poolConfig.setMinIdle(16);
    val r = new JedisPool(poolConfig, sys.env("REDIS_HOST"), sys.env("REDIS_PORT").toInt)

   Some(r)
  }

  def fromOptions(maybeTimestamp: Option[Long], maybeUser: Option[String], maybeEvent: Option[Event]): Option[AnalyticsDatapoint] = {
    for {
      timestamp <- maybeTimestamp
      user <- maybeUser
      event <- maybeEvent
    } yield AnalyticsDatapoint(timestamp, user, event)
  }

  def getEventsOfType(event: Event, timeframe: (Double, Double)): Option[Int] = {
    maybeConnection.map(connection => {
      val jedisInst = connection.getResource();
      val size: Int = jedisInst.zrangeByScore(event.getName, timeframe._1, timeframe._2).size
      jedisInst.close();

      size;
    })
  }

  def getUniqueUsers(timeframe: (Double, Double)): Option[Int] = {
    maybeConnection.map(connection => {
      val jedisInst = connection.getResource();
      val size: Int = jedisInst.zrangeByScore("users", timeframe._1, timeframe._2).size
      jedisInst.close();

      size;
    })
  }

}