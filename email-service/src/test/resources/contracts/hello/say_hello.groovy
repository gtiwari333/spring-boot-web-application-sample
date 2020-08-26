import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description "should say hello"

    request {
        url "/test/hello"
        method GET()
    }

    response {
        status OK()
        headers {
            contentType applicationJson()
        }
        body(
            hello: "world"
        )
    }
}
