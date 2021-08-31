package gt.app.config;

import gt.app.config.logging.HibernateStatInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.orm.jpa.HibernatePropertiesCustomizer;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
@Profile("!test")
public class AppHibernatePropertiesCustomizer implements HibernatePropertiesCustomizer {

    private final HibernateStatInterceptor statInterceptor;

    @Override
    public void customize(Map<String, Object> props) {

        props.put("hibernate.session_factory.interceptor", statInterceptor);
    }
}
