package gt.app.config;

import gt.common.config.PaginationCustomizer;
import gt.common.config.ReqLogFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.data.web.config.PageableHandlerMethodArgumentResolverCustomizer;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import java.util.Locale;

@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

    private final WebProperties webProperties;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("favicon.ico")
            .addResourceLocations(this.webProperties.getResources().getStaticLocations());
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(localeInterceptor());
    }

    public HandlerInterceptor localeInterceptor() {
        LocaleChangeInterceptor localeInt = new LocaleChangeInterceptor();
        localeInt.setParamName("lang");
        return localeInt;
    }

    @Bean
    public LocaleResolver localeResolver() {
        SessionLocaleResolver slr = new SessionLocaleResolver();
        slr.setDefaultLocale(Locale.US);
        return slr;
    }

    @Bean
    public PageableHandlerMethodArgumentResolverCustomizer paginationCustomizer() {
        return new PaginationCustomizer();
    }

    @Bean
    public FilterRegistrationBean<ReqLogFilter> loggingFilter() {
        var registrationBean = new FilterRegistrationBean<ReqLogFilter>();

        registrationBean.setFilter(new ReqLogFilter());
        registrationBean.setOrder((Ordered.HIGHEST_PRECEDENCE));

        return registrationBean;
    }
}
