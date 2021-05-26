package gt.app.api;

import gt.api.email.EmailDto;
import gt.api.email.EmailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@FeignClient(name = "email-service", url = "${feign-clients.email-service.url}", fallback = EmailClientFallback.class)
public interface EmailClient extends EmailService {
}

@Slf4j
class EmailClientFallback implements EmailClient {

    @Override
    public ResponseEntity<Void> sendEmailWithAttachments(@Valid @NotNull EmailDto email) {
        log.debug("sending email to nowhere {}", email);
        return ResponseEntity.noContent().build();
    }
}
