package gt.simulations

import gt.Environment
import gt.scenarios.{ArticleScenario, HomePageScenario}
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.http.protocol.HttpProtocolBuilder

import scala.concurrent.duration.DurationInt

class PublicUserSimulation extends Simulation {

    val httpConfig: HttpProtocolBuilder = http.baseUrl(Environment.baseUrl)

    //scenario
    val scn = scenario("open public home page read article and navigate to other pages")
        .exec(HomePageScenario.homePage())
        .exec(ArticleScenario.readArticle(6))
        .pause(10.milliseconds, 500.milliseconds)
        .exec(ArticleScenario.readArticle(5))
        .pause(100.milliseconds, 500.milliseconds)
        .exec(ArticleScenario.readArticle(4))
        .pause(100.milliseconds, 500.milliseconds)
        .exec(ArticleScenario.readArticle(3))
        .pause(100.milliseconds, 500.milliseconds)
        .exec(HomePageScenario.openPage(2))
        .pause(100.milliseconds, 500.milliseconds)
        .exec(HomePageScenario.openPage(3))
        .pause(100.milliseconds, 500.milliseconds)

    before {
        //data prep
    }

    setUp(
        scn.inject(
            //load scenario
            nothingFor(1 seconds),
            atOnceUsers(5),
            rampUsers(Environment.maxUsers) during Environment.rampUpTime
        ).protocols(httpConfig.inferHtmlResources()) // inferHtmlResources will fetch everything on the page (JS, CSS, images etc.)
    )

}
