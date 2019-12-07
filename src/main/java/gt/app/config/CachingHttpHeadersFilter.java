package gt.app.config;

import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

class CachingHttpHeadersFilter extends OncePerRequestFilter {

    final long lastModified = System.currentTimeMillis();
    final long cacheTimeToLive = TimeUnit.DAYS.toMillis(1461L);

    @Override
    public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
        throws IOException, ServletException {

        response.setHeader("Cache-Control", "max-age=" + cacheTimeToLive + ", public");
        response.setHeader("Pragma", "cache");

        // Setting Expires header, for proxy caching
        response.setDateHeader("Expires", cacheTimeToLive + System.currentTimeMillis());

        // Setting the Last-Modified header, for browser caching
        response.setDateHeader("Last-Modified", lastModified);

        filterChain.doFilter(request, response);
    }
}
