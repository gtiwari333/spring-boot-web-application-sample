package gt

import com.github.javafaker.Faker

import scala.concurrent.duration._

object Environment {

    def envOrElse(name: String, alt: String): String = Option(System.getenv(name))
        .orElse(Option(System.getProperty(name)))
        .getOrElse(alt)

    val profile: String = envOrElse("profile", "local")
    val rampUpTime: FiniteDuration = envOrElse("rampUpTIme", "10").toInt seconds
    val maxUsers: Int = envOrElse("users", "500").toInt

    val faker = new Faker()

    val keycloakUrl = "http://localhost:8082/"
    val appLogoutUrl = "http://localhost:8081/sso/logout"
    val appLoginUrl = "http://localhost:8081/sso/login"
    var baseUrl: String = "/"
    var publicHomePage: String = "/"
    var newArticleUrl: String = "/article/add"
    var userArticlesPage: String = "/article"

    if (profile == "local") {
        baseUrl = "http://localhost:8081"
        publicHomePage = "/"
    }
    //customize for another profile...
}
