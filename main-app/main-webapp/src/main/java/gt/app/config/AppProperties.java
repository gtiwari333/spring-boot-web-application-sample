package gt.app.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

@ConfigurationProperties(prefix = "app-properties", ignoreUnknownFields = false)
@Data
public class AppProperties {

    final FileStorage fileStorage = new FileStorage();
    final JmsProps jms = new JmsProps();
    final Email email = new Email();
    final Security security = new Security();

    @Data
    public static class Security {
        private String contentSecurityPolicy;
        private List<String> oauth2Audience;
    }

    @Data
    public static class FileStorage {
        String uploadFolder;
    }

    @Data
    public static class JmsProps {
        String contentCheckerRequestQueue;
        String contentCheckerCallBackResponseQueue;
    }

    @Data
    public static class Email {
        String authorNotificationsFromEmail = "no-reply@system";
        String authorNotificationsFromName = "Article App";
    }

}
