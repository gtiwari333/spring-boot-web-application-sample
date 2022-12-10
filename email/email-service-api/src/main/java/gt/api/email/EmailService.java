package gt.api.email;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

public interface EmailService {
    @PostMapping("/sendEmail")
    ResponseEntity<Void> sendEmailWithAttachments(@RequestBody @Valid @NotNull EmailDto email);
}
