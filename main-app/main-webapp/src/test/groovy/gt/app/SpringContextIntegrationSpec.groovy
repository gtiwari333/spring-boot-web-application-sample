package gt.app

import gt.app.web.rest.HelloResource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class SpringContextIntegrationSpec extends BaseIntegrationSpec {

    @Autowired(required = false)
    private HelloResource webController

    def "when context is loaded then all expected beans are created"() {
        expect: "the WebController is created"
        webController
    }
}
