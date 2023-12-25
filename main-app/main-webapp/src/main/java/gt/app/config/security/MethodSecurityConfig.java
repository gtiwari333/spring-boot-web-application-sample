package gt.app.config.security;

import gt.app.modules.user.AppPermissionEvaluatorService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

@Configuration
@EnableMethodSecurity(securedEnabled = true, jsr250Enabled = true)
class MethodSecurityConfig {

    private final AppPermissionEvaluatorService permissionEvaluator;
    private final ApplicationContext applicationContext;

    MethodSecurityConfig(@Qualifier("permEvaluator") AppPermissionEvaluatorService permissionEvaluator, ApplicationContext applicationContext) {
        this.permissionEvaluator = permissionEvaluator;
        this.applicationContext = applicationContext;
    }

    @Bean
    protected MethodSecurityExpressionHandler createExpressionHandler() {
        final DefaultMethodSecurityExpressionHandler expressionHandler = new DefaultMethodSecurityExpressionHandler();
        expressionHandler.setApplicationContext(applicationContext);
        expressionHandler.setPermissionEvaluator(permissionEvaluator);
        return expressionHandler;
    }

}
