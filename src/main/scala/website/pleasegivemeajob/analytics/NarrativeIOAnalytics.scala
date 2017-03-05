package website.pleasegivemeajob.analytics

import org.scalatra._

class NarrativeIOAnalytics extends AnalyticsStack {

  val id = scala.util.Random.alphanumeric.take(10).mkString;

  get("/whoami") {
    id
  }

  get("/analytics") {
    val HOUR_IN_MILLIS = 3600000;

    val timeframe: Option[(Double, Double)] = params
      .get("timestamp")
      .map(_.toDouble)
      .map(x => {
        val hourStart = Math.floor(x / HOUR_IN_MILLIS) * HOUR_IN_MILLIS;

        (hourStart, hourStart + HOUR_IN_MILLIS)
      })

    if (timeframe == None) {
      halt(404, "No data for the timeframe")
    } else {
      val clicks = timeframe.map(AnalyticsDatapoint.getEventsOfType(Click(), _)).flatten
      val impressions = timeframe.map(AnalyticsDatapoint.getEventsOfType(Impression(), _)).flatten
      val users = timeframe.map(AnalyticsDatapoint.getUniqueUsers(_)).flatten

      val result = for {
        c <- clicks
        i <- impressions
        u <- users
      } yield s"unique_users,${ u }\nclicks,${ c }\nimpressions,${ i }"

      result.getOrElse("No data for the timeframe")
    }
  }

  post("/analytics") {
    AnalyticsDatapoint.fromOptions(
      params.get("timestamp").map(_.toLong),
      params.get("user"),
      params.get("event")
    ).foreach(datapoint => {
      datapoint.writeToRedis();
    })
    halt(204)
  }

}


