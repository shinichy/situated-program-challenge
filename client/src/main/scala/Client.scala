import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.util.ByteString
import io.circe.Json
import io.circe.syntax._
import play.api.libs.ws.ahc._
import play.api.libs.ws.{BodyWritable, InMemoryBody, StandaloneWSRequest}

import scala.concurrent.ExecutionContext.Implicits._
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}


object Client extends App {
  implicit val writeableOf_Json: BodyWritable[Json] = {
    BodyWritable(json => InMemoryBody(ByteString.fromString(json.noSpaces)), "application/json")
  }

  val paramRegex = """([^=]+)=([^=]+)""".r

  withWsClient { wsClient =>
    args match {
      case Array(url, method, params@_*) =>
        val paramPairs = params map { case paramRegex(key, value) => (key, value) }
        val request = wsClient.url(url)

        (method match {
          case "GET" =>
            request.withQueryStringParameters(paramPairs: _*).get()
          case "POST" =>
            val json = paramPairs.toMap.asJson
            request.post(json)
        }) map printResponse

      case _ => Future.successful(println("usage: URL [GET|POST] key1=value1 key2=value2.."))
    }
  }

  private def withWsClient(f: StandaloneAhcWSClient => Future[Unit]): Unit = {
    implicit val system = ActorSystem()

    implicit val materializer = ActorMaterializer()

    val wsClient = StandaloneAhcWSClient()

    try {
      Await.ready(f(wsClient), Duration.Inf)
    } finally {
      wsClient.close()
      system.terminate()
    }
  }

  private def printResponse(response: StandaloneWSRequest#Response) = {
    if (response.status == 200) {
      println(response.body)
    } else {
      println(s"${response.status}")
    }
  }
}
