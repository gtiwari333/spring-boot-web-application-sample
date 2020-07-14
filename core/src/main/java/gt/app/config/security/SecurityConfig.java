package gt.app.config.security;

import gt.app.config.Constants;
import gt.app.modules.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.keycloak.adapters.*;
import org.keycloak.adapters.springsecurity.KeycloakConfiguration;
import org.keycloak.adapters.springsecurity.authentication.KeycloakAuthenticationProvider;
import org.keycloak.adapters.springsecurity.authentication.KeycloakLogoutHandler;
import org.keycloak.adapters.springsecurity.config.KeycloakWebSecurityConfigurerAdapter;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.keycloak.representations.adapters.config.AdapterConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.web.authentication.session.RegisterSessionAuthenticationStrategy;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private static final String[] AUTH_WHITELIST = {
        "/swagger-resources/**",
        "/v2/api-docs",
        "/swagger-ui.html",
        "/h2-console/**",
        "/webjars/**",
        "/favicon.ico",
        "/static/**",
        "/wro4j/**",
        "/" //landing page is allowed for all
    };


    @Bean
    public KeycloakConfigResolver kcSBConfigResolver(AdapterConfig adapterConfig) {
        //FIXME: temporary fix until 11.0.0 is released  https://stackoverflow.com/questions/61228097/npe-when-loading-custom-securityconfig-for-keycloak-in-webmvctest
        return new SpringBootKeycloakConfigResolver(adapterConfig);
    }

    @RequiredArgsConstructor
    static class SpringBootKeycloakConfigResolver implements KeycloakConfigResolver {
        private KeycloakDeployment keycloakDeployment;
        private final AdapterConfig adapterConfig;

        @Override
        public KeycloakDeployment resolve(OIDCHttpFacade.Request request) {
            if (keycloakDeployment != null) {
                return keycloakDeployment;
            }
            keycloakDeployment = KeycloakDeploymentBuilder.build(adapterConfig);
            return keycloakDeployment;
        }
    }

    @KeycloakConfiguration
    @RequiredArgsConstructor
    static class KCSecurityConfig extends KeycloakWebSecurityConfigurerAdapter {

        final UserService userService;

        @Override
        protected KeycloakAuthenticationProvider keycloakAuthenticationProvider() {
            return new AppKeycloakAuthProvider(userService);
        }

        @Override
        protected SessionAuthenticationStrategy sessionAuthenticationStrategy() {
            return new RegisterSessionAuthenticationStrategy(new SessionRegistryImpl());
        }

        @Override
        protected void configure(AuthenticationManagerBuilder auth) throws Exception {
            auth.authenticationProvider(keycloakAuthenticationProvider());
        }

        @Override
        protected KeycloakLogoutHandler keycloakLogoutHandler() throws Exception {
            return new LogoutHandler(adapterDeploymentContext());
        }


        @Override
        protected void configure(HttpSecurity http) throws Exception {

            super.configure(http);

            http
                .headers().frameOptions().sameOrigin()
                .and()
                    .authorizeRequests()
                    .antMatchers(AUTH_WHITELIST).permitAll()
                    .antMatchers("/admin/**").hasAuthority(Constants.ROLE_ADMIN)
                    .antMatchers("/user/**").hasAuthority(Constants.ROLE_USER)
                    .antMatchers("/api/**").authenticated()//individual api will be secured differently
                    .antMatchers("/public/**").permitAll()
                    .anyRequest().authenticated() //this one will catch the rest patterns
                .and()
                    .csrf().disable();


        }

        static class LogoutHandler extends KeycloakLogoutHandler {

            public LogoutHandler(AdapterDeploymentContext adapterDeploymentContext) {
                super(adapterDeploymentContext);
            }

            @SneakyThrows
            @Override
            protected void handleSingleSignOut(HttpServletRequest request, HttpServletResponse response, KeycloakAuthenticationToken authenticationToken) {
                super.handleSingleSignOut(request, response, authenticationToken);
                response.sendRedirect("/?logout=true");
            }
        }


    }

}

