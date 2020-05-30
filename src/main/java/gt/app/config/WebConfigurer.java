package gt.app.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;

import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;
import javax.servlet.ServletContext;
import java.util.EnumSet;

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

    @Bean
    public FilterRegistrationBean<ReqLogFilter> loggingFilter() {
        FilterRegistrationBean<ReqLogFilter> registrationBean = new FilterRegistrationBean<>();

        registrationBean.setFilter(new ReqLogFilter());
        registrationBean.setOrder((Ordered.HIGHEST_PRECEDENCE));

        return registrationBean;
    }

}
