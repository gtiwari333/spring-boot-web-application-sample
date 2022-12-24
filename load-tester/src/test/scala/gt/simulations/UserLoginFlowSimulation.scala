package gt.simulations

import gt.Environment
import gt.scenarios.LoginScenarios
import io.gatling.core.Predef._
import io.gatling.core.structure.ScenarioBuilder
import io.gatling.http.Predef._
import io.gatling.http.protocol.HttpProtocolBuilder

import scala.concurrent.duration.DurationInt

class UserLoginFlowSimulation extends Simulation {

    val httpProtocol: HttpProtocolBuilder = http
        .baseUrl(Environment.baseUrl)

    val scn: ScenarioBuilder = scenario("login-read-logout")
        .feed(LoginScenarios.userFeeder())
        .exec(LoginScenarios.loadLoginPage())
        .pause(50.milliseconds, 500.milliseconds)
        .exec(LoginScenarios.userAuthenticate())
        .pause(50.milliseconds, 500.milliseconds)
        .exec(http("get home page after login")
            .get(Environment.publicHomePage)
            .check(status.is(200))
        )
        .exec(http("get secured articles page after login")
            .get(Environment.userArticlesPage)
            .check(status.is(200))
        )
        .pause(50.milliseconds, 500.milliseconds)
        .exec(LoginScenarios.logout())


    //TODO:
    //perform create article
    //grab id of newly created article
        // use data feeder use -- Java Faker
    //read new articles based on id

    before {
        //data prep
    }

    setUp(
        scn.inject(
            //load scenario
            nothingFor(1 seconds),
            atOnceUsers(5),
            rampUsers(Environment.maxUsers) during Environment.rampUpTime
        ).protocols(httpProtocol)
    )
}
