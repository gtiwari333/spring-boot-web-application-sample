package gt.common.config;

import lombok.extern.slf4j.Slf4j;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

@Slf4j
public
class ReqLogFilter implements Filter {

    private Consumer<PageView> logConsumer;

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        Map<String, String> req = new HashMap<>();

        req.put("req.remoteHost", request.getRemoteHost());

        if (request instanceof HttpServletRequest sreq) {
            req.put("req.requestURI", sreq.getRequestURI());

            StringBuffer requestURL = sreq.getRequestURL();
            if (requestURL != null) {
                req.put("req.requestURL", requestURL.toString());
            }
            req.put("req.method", sreq.getMethod());
            req.put("req.req.queryString", sreq.getQueryString());
            req.put("req.userAgent", sreq.getHeader("User-Agent"));
            req.put("req.xForwardedFor", sreq.getHeader("X-Forwarded-For"));
            req.put("req.x-b3-spanid", sreq.getHeader("x-b3-spanid"));
            req.put("req.x-b3-traceid", sreq.getHeader("x-b3-traceid"));

            if (logConsumer != null) {
                logConsumer.accept(new PageView(
                    request.getRemoteHost(),
                    sreq.getRequestURI(),
                    req.get("req.requestURL"),
                    sreq.getMethod(),
                    sreq.getQueryString(),
                    sreq.getHeader("User-Agent"),
                    sreq.getHeader("X-Forwarded-For")
                ));
            }
        }

        log.info("Received request {} ", req);

        chain.doFilter(request, response);
    }

    public void setLogConsumer(Consumer<PageView> logConsumer) {
        this.logConsumer = logConsumer;
    }
}
