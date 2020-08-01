package gt.common.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;

/**
 * Utility class for HTTP headers creation.
 */
@Slf4j
public final class HeaderUtil {

    private static final String APPLICATION_NAME = "BlogApp";//No Spaces here..

    private HeaderUtil() {
    }

    public static HttpHeaders alertMsg(String message) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-" + APPLICATION_NAME + "-alert", message);
        return headers;
    }

    public static HttpHeaders created(String entityName) {
        return alertMsg("A new " + entityName + " has been created ");
    }

    public static HttpHeaders updated(String entityName) {
        return alertMsg("The " + entityName + " has been updated ");
    }

    public static HttpHeaders deleted(String entityName) {
        return alertMsg("The " + entityName + " has been deleted ");
    }

    public static HttpHeaders failureMsg(String message) {
        log.error("Entity processing failed, {}", message);
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-" + APPLICATION_NAME + "-error", message);
        return headers;
    }
}
