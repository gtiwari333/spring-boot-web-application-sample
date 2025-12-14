package gt.app.config.metrics;

import gt.app.config.logging.HibernateStatementStatInterceptor;
import gt.app.config.logging.WebRequestInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.cfg.AvailableSettings;
import org.springframework.boot.hibernate.autoconfigure.HibernatePropertiesCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@Profile("!test")
@Slf4j
class RequestStatisticsConfiguration implements WebMvcConfigurer {

    @Bean
    public HibernateStatementStatInterceptor hibernateInterceptor() {
        return new HibernateStatementStatInterceptor();
    }

    @Bean
    public WebRequestInterceptor requestStatisticsInterceptor() {
        return new WebRequestInterceptor(hibernateInterceptor());
    }

    @Bean
    public HibernatePropertiesCustomizer hibernateCustomizer(HibernateStatementStatInterceptor statInterceptor) {
        return (properties) -> properties.put(AvailableSettings.STATEMENT_INSPECTOR, statInterceptor);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(requestStatisticsInterceptor()).addPathPatterns("/", "/article/**");
    }

}
