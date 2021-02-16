package gt.simulations

import gt.Environment
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.http.request.builder.HttpRequestBuilder

import scala.concurrent.duration.DurationInt

class PublicUserSimulation extends BaseSimulation {

    def openPage(page: Int): HttpRequestBuilder = {
        http("open page " + page)
            .get("/?size=5&page=" + (page - 1))
    }

    def readArticle(id: Int): HttpRequestBuilder = {
        http("read article #" + id)
            .get("/article/read/" + id)
    }

    //scenario
    val scn = scenario("Open Public Home Page")
        .exec(http("public home page")
            .get(Environment.publicHomePage))
        .exec(readArticle(68))
        .exec(readArticle(67))
        .exec(readArticle(66))
        .exec(openPage(2))
        .exec(openPage(3))

    before {
        //data prep
    }

    setUp(
        scn.inject(
            //load scenario
            nothingFor(1 seconds),
            atOnceUsers(5),
            rampUsers(Environment.maxUsers) during (Environment.rampUpTime)
        ).protocols(httpConfig.inferHtmlResources()) // inferHtmlResources will fetch everything on the page (JS, CSS, images etc.)
    )

}
