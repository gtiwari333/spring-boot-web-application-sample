package gt.simulations

import gt.Environment
import io.gatling.core.Predef._
import io.gatling.core.scenario.Simulation
import io.gatling.http.Predef.http
import io.gatling.http.protocol.HttpProtocolBuilder

class BaseSimulation extends Simulation {
    //http
    val httpConfig: HttpProtocolBuilder = http.baseUrl(Environment.baseUrl)

}
