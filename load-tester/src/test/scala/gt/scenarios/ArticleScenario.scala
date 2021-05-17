package gt.scenarios

import gt.Environment
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.http.request.builder.HttpRequestBuilder

object ArticleScenario {

    def readArticle(id: Int): HttpRequestBuilder = {
        http("read article #" + id)
            .get("/article/read/" + id)
    }

    def allUserArticles(): HttpRequestBuilder = http("get secured articles page")
        .get(Environment.userArticlesPage)
        .check(status.is(200))

    def newArticle(): HttpRequestBuilder = http("create new article")
        .post(Environment.newArticleUrl)
        .formParam("title", Environment.faker.book().title())
        .formParam("content", Environment.faker.gameOfThrones().quote())
        .check(status.is(200))

    //other methods
}
