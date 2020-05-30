package gt.app.config;

import lombok.extern.slf4j.Slf4j;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
class ReqLogFilter implements Filter {

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        Map<String, String> req = new HashMap<>();

        req.put("req.remoteHost", request.getRemoteHost());

        if (request instanceof HttpServletRequest) {
            HttpServletRequest httpServletRequest = (HttpServletRequest) request;
            req.put("req.requestURI", httpServletRequest.getRequestURI());
            StringBuffer requestURL = httpServletRequest.getRequestURL();
            if (requestURL != null) {
                req.put("req.requestURL", requestURL.toString());
            }
            req.put("req.method", httpServletRequest.getMethod());
            req.put("req.req.queryString", httpServletRequest.getQueryString());
            req.put("req.userAgent", httpServletRequest.getHeader("User-Agent"));
            req.put("req.xForwardedFor", httpServletRequest.getHeader("X-Forwarded-For"));
        }

        log.info("Received request {} ", req);


        chain.doFilter(request, response);

    }

}
