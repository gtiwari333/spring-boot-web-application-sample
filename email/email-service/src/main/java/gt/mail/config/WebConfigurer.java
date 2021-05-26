package gt.mail.config;

import gt.common.config.ReqLogFilter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class WebConfigurer {

    @Bean
    public FilterRegistrationBean<ReqLogFilter> loggingFilter() {
        FilterRegistrationBean<ReqLogFilter> registrationBean = new FilterRegistrationBean<>();

        registrationBean.setFilter(new ReqLogFilter());
        registrationBean.setOrder((Ordered.HIGHEST_PRECEDENCE));

        return registrationBean;
    }

}
