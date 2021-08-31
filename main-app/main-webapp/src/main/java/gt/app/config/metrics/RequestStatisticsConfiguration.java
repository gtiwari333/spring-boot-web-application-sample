package gt.app.config.metrics;

import gt.app.config.logging.HibernateStatInterceptor;
import gt.app.config.logging.WebRequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@Profile("!test")
public class RequestStatisticsConfiguration implements WebMvcConfigurer {

    @Bean
    public HibernateStatInterceptor hibernateInterceptor() {
        return new HibernateStatInterceptor();
    }

    @Bean
    public WebRequestInterceptor requestStatisticsInterceptor() {
        return new WebRequestInterceptor(hibernateInterceptor());
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(requestStatisticsInterceptor()).addPathPatterns("/", "/article/**");
    }

}
