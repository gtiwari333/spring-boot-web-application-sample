package gt.scenarios

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.http.request.builder.HttpRequestBuilder

object ArticleScenario {

    def readArticle(id: Int): HttpRequestBuilder = {
        http("read article #" + id)
            .get("/article/read/" + id)
    }

    //other methods
}
