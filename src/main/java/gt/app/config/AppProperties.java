package gt.app.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "app-properties", ignoreUnknownFields = false)
@Data
public class AppProperties {

    final FileStorage fileStorage = new FileStorage();

    @Data
    public static class FileStorage {
        String uploadFolder;
    }
}
