package gt.app.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.EnumSet;
import java.util.concurrent.TimeUnit;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class WebConfigurer implements ServletContextInitializer {

    private final Environment env;

    @Override
    public void onStartup(ServletContext servletContext) {

        EnumSet<DispatcherType> disps = EnumSet.of(DispatcherType.REQUEST, DispatcherType.FORWARD, DispatcherType.ASYNC);

        if (env.acceptsProfiles(Profiles.of(Constants.SPRING_PROFILE_PRODUCTION, Constants.SPRING_PROFILE_DOCKER))) {
            initCachingHttpHeadersFilter(servletContext, disps);
        }
    }

    private void initCachingHttpHeadersFilter(ServletContext servletContext, EnumSet<DispatcherType> disps) {
        log.debug("Registering Caching HTTP Headers Filter");
        FilterRegistration.Dynamic cachingHttpHeadersFilter = servletContext.addFilter("cachingHttpHeadersFilter", new CachingHttpHeadersFilter());

        cachingHttpHeadersFilter.addMappingForUrlPatterns(disps, true, "/webjars/*");
        cachingHttpHeadersFilter.addMappingForUrlPatterns(disps, true, "/static/*");
        cachingHttpHeadersFilter.setAsyncSupported(true);
    }

    static class CachingHttpHeadersFilter extends OncePerRequestFilter {

        private final long LAST_MODIFIED = System.currentTimeMillis();
        private long cacheTimeToLive = TimeUnit.DAYS.toMillis(1461L);

        @Override
        public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws IOException, ServletException {

            response.setHeader("Cache-Control", "max-age=" + cacheTimeToLive + ", public");
            response.setHeader("Pragma", "cache");

            // Setting Expires header, for proxy caching
            response.setDateHeader("Expires", cacheTimeToLive + System.currentTimeMillis());

            // Setting the Last-Modified header, for browser caching
            response.setDateHeader("Last-Modified", LAST_MODIFIED);

            filterChain.doFilter(request, response);
        }
    }

}
