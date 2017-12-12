import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.util.ByteString
import io.circe.Json
import io.circe.parser._
import play.api.libs.ws.DefaultBodyWritables._
import play.api.libs.ws.ahc._
import play.api.libs.ws.{BodyWritable, InMemoryBody, StandaloneWSRequest}

import scala.concurrent.ExecutionContext.Implicits._
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}
import scala.io.Source


object Client extends App {
  implicit val writeableOf_Json: BodyWritable[Json] = {
    BodyWritable(json => InMemoryBody(ByteString.fromString(json.noSpaces)), "application/json")
  }

  val paramRegex = """([^=]+)=([^=]+)""".r

  withWsClient { wsClient =>
    args match {
      case Array(url, "GET", params@_*) =>
        val paramPairs = params map { case paramRegex(key, value) => (key, value) }
        wsClient.url(url).withQueryStringParameters(paramPairs: _*).get() map printResponse

      case Array(url, "POST") =>
        val request = wsClient.url(url)

        val future = if (System.in.available() > 0) {
          val str = Source.stdin.getLines.mkString
          val json = parse(str).toTry.get
          request.post(json)
        } else {
          request.post("")
        }

        future map printResponse

      case _ => Future.successful(println("usage: URL GET key1=value1 key2=value2"))
    }
  }

  private def withWsClient(f: StandaloneAhcWSClient => Future[Unit]): Unit = {
    implicit val system: ActorSystem = ActorSystem()

    implicit val materializer: ActorMaterializer = ActorMaterializer()

    val wsClient = StandaloneAhcWSClient()

    try {
      Await.result(f(wsClient), Duration.Inf)
    } catch {
      case e: Throwable => println(e.getMessage)
    } finally {
      wsClient.close()
      system.terminate()
    }
  }

  private def printResponse(response: StandaloneWSRequest#Response): Unit = {
    if (response.status == 200) {
      println(response.body)
    } else {
      println(s"${response.status}")
    }
  }
}
