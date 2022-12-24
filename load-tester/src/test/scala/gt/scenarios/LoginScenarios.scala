package gt.scenarios

import gt.Environment
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.http.request.builder.HttpRequestBuilder

import java.util.concurrent.ThreadLocalRandom

object LoginScenarios {

    def userFeeder(): Iterator[Map[String, String]] = Iterator.continually({
        val users = Array("user1", "user2")
        Map(
            "userName" -> users(ThreadLocalRandom.current().nextInt(users.length))
        )
    })

    def loadLoginPage(): HttpRequestBuilder = http("app-login-page")
        .get(Environment.appLoginUrl)
        .check(status.is(200))
        .check(css(".form-signin")
            .ofType[Node]
            .transform(n => {
                n.getAttribute("action")
            }).saveAs("loginActionHandler"))

    def userAuthenticate(): HttpRequestBuilder = http("authentication")
        .post("${loginActionHandler}")
        .formParam("username", "${userName}")
        .formParam("password", "pass")
        .check(status.is(200))


    def logout(): HttpRequestBuilder = http("app-logout")
        .get(Environment.appLogoutUrl)
        .check(status.is(200))

}
