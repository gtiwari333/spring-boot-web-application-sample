package gt.mail.web.rest;

import gt.api.email.EmailDto;
import gt.mail.modules.email.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import static gt.mail.utils.EmailUtil.toInternetAddr;


@RestController
@RequiredArgsConstructor
@Slf4j
public class EmailController implements gt.api.email.EmailService {
    private final EmailService emailService;

    @PostMapping("/sendEmail")
    public ResponseEntity<Void> sendEmailWithAttachments(@RequestBody @Valid @NotNull EmailDto email) {
        log.debug("Sending email ...");

        emailService.queue(toInternetAddr(email.getTo()), toInternetAddr(email.getCc()), toInternetAddr(email.getBcc()),
            toInternetAddr().apply(email.getFrom()), email.getSubject(), email.getContent(), email.getFiles(), email.isHtml());

        return ResponseEntity.ok().build();
    }

}
