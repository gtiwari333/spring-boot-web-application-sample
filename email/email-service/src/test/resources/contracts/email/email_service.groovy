package contracts.email


import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description "should receive request"

    request {
        url "/sendEmail"
        method POST()
        body([
            from   : 'test@email.com',
            subject: 'Test',
            content: 'Body',
            isHtml : false,
            files  : [],
            to     : ['recep@emai.com'],
            cc     : [],
            bcc    : [],

        ])
        headers {
            contentType applicationJson()
        }
    }

    response {
        status OK()
    }
}
