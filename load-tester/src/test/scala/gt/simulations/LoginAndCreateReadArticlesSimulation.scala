package gt.simulations

import gt.Environment
import gt.scenarios.{ArticleScenario, HomePageScenario, KeyCloakLoginScenarios}
import io.gatling.core.Predef._
import io.gatling.core.structure.ScenarioBuilder
import io.gatling.http.Predef._
import io.gatling.http.protocol.HttpProtocolBuilder

import scala.concurrent.duration.DurationInt

class LoginAndCreateReadArticlesSimulation extends Simulation {

    val httpProtocol: HttpProtocolBuilder = http
        .baseUrl(Environment.baseUrl)

    val scn: ScenarioBuilder = scenario("keycloak-standard-flow")
        .feed(KeyCloakLoginScenarios.userFeeder())
        .exec(KeyCloakLoginScenarios.loadLoginPage())
        .pause(100.milliseconds, 300.milliseconds)
        .exec(KeyCloakLoginScenarios.keyCloakAuthenticate())
        .pause(50.milliseconds, 100.milliseconds)
        .exec(HomePageScenario.homePage())
        .exec(ArticleScenario.allUserArticles())
        .pause(50.milliseconds, 100.milliseconds)
        .exec(ArticleScenario.newArticle())
        .pause(50.milliseconds, 100.milliseconds)
        .exec(ArticleScenario.allUserArticles())
        .pause(50.milliseconds, 100.milliseconds)
        .exec(KeyCloakLoginScenarios.logout())


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
