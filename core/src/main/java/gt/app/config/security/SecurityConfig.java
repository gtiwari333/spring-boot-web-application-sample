package gt.app.config.security;

import gt.app.config.Constants;
import lombok.RequiredArgsConstructor;
import org.keycloak.adapters.springboot.KeycloakSpringBootConfigResolver;
import org.keycloak.adapters.springsecurity.KeycloakConfiguration;
import org.keycloak.adapters.springsecurity.authentication.KeycloakAuthenticationProvider;
import org.keycloak.adapters.springsecurity.config.KeycloakWebSecurityConfigurerAdapter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.web.authentication.session.RegisterSessionAuthenticationStrategy;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private static final String[] AUTH_WHITELIST = {
        "/swagger-resources/**",
        "/v2/api-docs",
        "/h2-console/**",
        "/webjars/**",
        "/static/**",
        "/" //landing page is allowed for all
    };


    @Bean
    public KeycloakSpringBootConfigResolver kcSBConfigResolver() {
        return new KeycloakSpringBootConfigResolver();
    }

    @KeycloakConfiguration
    static class KCSecurityConfig extends KeycloakWebSecurityConfigurerAdapter {

        @Override
        protected KeycloakAuthenticationProvider keycloakAuthenticationProvider() {
            return new AppKeycloakAuthProvider();
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
                    .antMatchers(("/public/**")).permitAll()
                    .anyRequest().authenticated() //this one will catch the rest patterns
                .and()
                    .csrf().disable();


        }

    }

}

