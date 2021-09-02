package gt.mail.web.rest;

import gt.api.email.EmailDto;
import gt.mail.modules.email.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@RestController
@RequiredArgsConstructor
@Slf4j
public class EmailController implements gt.api.email.EmailService {
    private final EmailService emailService;

    @PostMapping(value = "/sendEmail", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> sendEmailWithAttachments(@RequestBody @Valid @NotNull EmailDto email) {
        log.debug("Sending email ...");

        emailService.sendEmail(email);

        return ResponseEntity.ok().build();
    }

}
