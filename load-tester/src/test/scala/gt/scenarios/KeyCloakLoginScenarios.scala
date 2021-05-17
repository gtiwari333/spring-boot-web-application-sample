package gt.scenarios

import gt.Environment
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.http.request.builder.HttpRequestBuilder

import java.util.concurrent.ThreadLocalRandom

object KeyCloakLoginScenarios {

    def userFeeder(): Iterator[Map[String, String]] = Iterator.continually({
        val users = Array("user1", "user2")
        Map(
            "userName" -> users(ThreadLocalRandom.current().nextInt(users.length))
        )
    })

    def loadLoginPage(): HttpRequestBuilder = http("app-login-page")
        .get(Environment.appLoginUrl)
        .check(status.is(200))
        .check(css("#kc-form-login")
            .ofType[Node]
            .transform(n => {
                n.getAttribute("action")
            }).saveAs("keycloak_auth_url"))

    def keyCloakAuthenticate(): HttpRequestBuilder = http("keycloak-authentication")
        .post("${keycloak_auth_url}")
        .formParam("username", "${userName}")
        .formParam("password", "pass")
        .check(status.is(200))


    def logout(): HttpRequestBuilder = http("app-logout")
        .get(Environment.appLogoutUrl)
        .check(status.is(200))

}
