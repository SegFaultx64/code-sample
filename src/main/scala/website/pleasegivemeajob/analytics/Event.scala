package website.pleasegivemeajob.analytics

object Event {
  implicit def maybeString2Event(x: Option[String]): Option[Event] = {
    x.map( _ match {
      case "click" => Some(Click())
      case "impression" => Some(Impression())
      case _ => None
    }).flatten
  }
}

abstract class Event() {

  def getName(): String;

};

case class Click() extends Event {
  def getName = "click"
};
case class Impression() extends Event {
  def getName = "impression"
};

