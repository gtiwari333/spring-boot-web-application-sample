package g.t.app.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app-properties", ignoreUnknownFields = false)
@Data
public class AppProperties {

    final FileStorage fileStorage;

    @Data
    public static class FileStorage {
        String uploadFolder;
    }
}
