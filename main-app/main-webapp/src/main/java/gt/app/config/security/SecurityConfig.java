package gt.app.config.security;

import gt.app.config.Constants;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true)
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
        "/signup/**",
        "/error/**",
        "/public/**",
        "/article/read/**",
        "/download/file/**",
        "/" //landing page is allowed for all
    };

    @Bean
    protected SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .headers().frameOptions().sameOrigin()
            .and()
                .authorizeHttpRequests()
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .requestMatchers(AUTH_WHITELIST).permitAll()
                .requestMatchers("/admin/**").hasAuthority(Constants.ROLE_ADMIN)
                .requestMatchers("/user/**").hasAuthority(Constants.ROLE_USER)
                .requestMatchers("/api/**").authenticated()//individual api will be secured differently
                .anyRequest().authenticated() //this one will catch the rest patterns
            .and()
                .csrf().disable()
            .formLogin()
                .loginProcessingUrl("/auth/login")
                .permitAll()
            .and()
                .logout()
                .logoutUrl("/auth/logout")
                .logoutSuccessUrl("/?logout")
                .permitAll();

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
