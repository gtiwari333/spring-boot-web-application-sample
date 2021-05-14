package gt.simulations

import io.gatling.core.Predef._
import io.gatling.http.Predef._

import scala.concurrent.duration.DurationInt

class TestSimulation extends Simulation {

    val httpProtocol = http
        .baseUrl("http://computer-database.gatling.io") // Here is the root for all relative URLs

    val scn = scenario("Scenario Name")
        .exec(http("get req").get("/"))

    setUp(
        scn.inject(
            nothingFor(1.seconds),
            atOnceUsers(2),
        ).protocols(httpProtocol)
    )

}
