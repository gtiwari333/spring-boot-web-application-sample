package gt.mail.web.rest;

import gt.mail.modules.email.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import static gt.mail.utils.EmailUtil.toInternetAddr;


@RestController
@RequiredArgsConstructor
@Slf4j
public class EmailController {
    private final EmailService emailService;

    @PostMapping("/sendEmail")
    public ResponseEntity<?> sendEmailWithAttachments(@Valid @NotNull EmailDto email) {

        emailService.queue(toInternetAddr(email.to), toInternetAddr(email.cc), toInternetAddr(email.bcc),
            toInternetAddr().apply(email.from), email.subject, email.content, email.files, email.isHtml);

        return ResponseEntity.ok().build();
    }

}
