package gt.api.email;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "email-service", url = "${feign-clients.email-service.url}")
public interface EmailClient extends EmailService {
}
