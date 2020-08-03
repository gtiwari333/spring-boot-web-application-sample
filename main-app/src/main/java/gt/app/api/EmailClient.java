package gt.app.api;

import gt.api.email.EmailService;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "email-service", url = "${feign-clients.email-service.url}")
public interface EmailClient extends EmailService {
}
