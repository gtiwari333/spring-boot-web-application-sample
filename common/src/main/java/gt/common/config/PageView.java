package gt.common.config;

public record PageView(String host,
                       String uri, String url, String method,
                       String queryString, String agent,
                       String forwarded) {
}
