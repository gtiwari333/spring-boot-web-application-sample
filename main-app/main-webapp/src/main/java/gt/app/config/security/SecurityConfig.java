package gt.app.config.security;

import gt.app.config.Constants;
import gt.app.modules.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.keycloak.adapters.AdapterDeploymentContext;
import org.keycloak.adapters.KeycloakConfigResolver;
import org.keycloak.adapters.springboot.KeycloakSpringBootConfigResolver;
import org.keycloak.adapters.springsecurity.KeycloakConfiguration;
import org.keycloak.adapters.springsecurity.authentication.KeycloakAuthenticationProvider;
import org.keycloak.adapters.springsecurity.authentication.KeycloakLogoutHandler;
import org.keycloak.adapters.springsecurity.config.KeycloakWebSecurityConfigurerAdapter;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.security.web.authentication.session.RegisterSessionAuthenticationStrategy;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.security.web.servletapi.SecurityContextHolderAwareRequestFilter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private static final String[] AUTH_WHITELIST = {
        "/swagger-resources/**",
        "/swagger-ui/**",
        "/swagger-ui.html",
        "/v3/api-docs/**",
        "/h2-console/**",
        "/webjars/**",
        "/favicon.ico",
        "/static/**",
        "/" //landing page is allowed for all
    };

    @Bean
    public KeycloakConfigResolver kcSBConfigResolver() {
        return new KeycloakSpringBootConfigResolver();
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

            /*
                removing `.addFilterAfter(keycloakAuthenticatedActionsRequestFilter(), KeycloakSecurityContextRequestFilter.class)` from base
                --> might get fixed in future keycloak adapter
                --> issues with keycloak adapter 13(based on Spring Security 3.2.7.RELEASE) + spring security 5.5(from SB 2.5)
             */
            http
                .csrf().requireCsrfProtectionMatcher(keycloakCsrfRequestMatcher())
                .and()
                    .sessionManagement()
                    .sessionAuthenticationStrategy(sessionAuthenticationStrategy())
                .and()
                    .addFilterBefore(keycloakPreAuthActionsFilter(), LogoutFilter.class)
                    .addFilterBefore(keycloakAuthenticationProcessingFilter(), LogoutFilter.class)
                    .addFilterAfter(keycloakSecurityContextRequestFilter(), SecurityContextHolderAwareRequestFilter.class)
                    .exceptionHandling().authenticationEntryPoint(authenticationEntryPoint())
                .and()
                    .logout()
                    .addLogoutHandler(keycloakLogoutHandler())
                    .logoutUrl("/sso/logout").permitAll()
                    .logoutSuccessUrl("/")

                .and()
                    .headers().frameOptions().sameOrigin()
                .and()
                    .authorizeRequests()
                    .antMatchers(AUTH_WHITELIST).permitAll()
                    .antMatchers("/admin/**").hasAuthority(Constants.ROLE_ADMIN)
                    .antMatchers("/user/**").hasAuthority(Constants.ROLE_USER)
                    .antMatchers("/api/**").authenticated()//individual api will be secured differently
                    .antMatchers("/public/**").permitAll()
                    .antMatchers("/article/read/**").permitAll()
                    .antMatchers("/download/file/**").permitAll()
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

