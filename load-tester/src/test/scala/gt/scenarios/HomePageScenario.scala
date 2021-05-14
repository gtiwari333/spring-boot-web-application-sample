package gt.scenarios

import gt.Environment
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.http.request.builder.HttpRequestBuilder

object HomePageScenario {

    def homePage(): HttpRequestBuilder = {
        http("public home page")
            .get(Environment.publicHomePage)
    }

    def openPage(page: Int): HttpRequestBuilder = {
        http("open page " + page)
            .get("/?size=5&page=" + (page - 1))
    }

    //other methods

}
