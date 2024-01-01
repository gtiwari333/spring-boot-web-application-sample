package gt.app.api;

import gt.api.email.EmailDto;
import gt.api.email.EmailService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;

@FeignClient(name = "email-service", url = "${feign-clients.email-service.url}", fallback = EmailClient.EmailClientFallback.class)
//doesn't use auth token
public interface EmailClient extends EmailService {
    @Slf4j
    class EmailClientFallback implements EmailClient {

        @Override
        public ResponseEntity<Void> sendEmailWithAttachments(@Valid @NotNull EmailDto email) {
            log.debug("sending email to nowhere {}", email);
            return ResponseEntity.noContent().build();
        }
    }

}

