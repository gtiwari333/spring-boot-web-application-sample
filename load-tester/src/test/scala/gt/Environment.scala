package gt

import net.datafaker.Faker

import scala.concurrent.duration._

object Environment {

    def envOrElse(name: String, alt: String): String = Option(System.getenv(name))
        .orElse(Option(System.getProperty(name)))
        .getOrElse(alt)

    val profile: String = envOrElse("profile", "local")
    val rampUpTime: FiniteDuration = envOrElse("rampUpTIme", "25").toInt seconds
    val maxUsers: Int = envOrElse("users", "1000").toInt

    val faker = new Faker()

    val appLogoutUrl = "http://localhost:8081/auth/logout"
    val appLoginUrl = "http://localhost:8081/login"
    var baseUrl: String = "/"
    var publicHomePage: String = "/"
    var newArticlePage: String = "/article/new"
    var newArticlePostUrl: String = "/article/add"
    var userArticlesPage: String = "/article"

    if (profile == "local") {
        baseUrl = "http://localhost:8081"
        publicHomePage = "/"
    }
    //customize for another profile...
}
