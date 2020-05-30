package gt.app.config.security;

import gt.app.config.Constants;
import gt.app.modules.user.AppUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@EnableWebSecurity
@Configuration
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private static final String[] AUTH_WHITELIST = {
        "/swagger-resources/**",
        "/v2/api-docs",
        "/h2-console/**",
        "/webjars/**",
        "/static/**",
        "/" //landing page is allowed for all
    };

    private final AppUserDetailsService appUserDetailsService;

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
        authenticationManagerBuilder
            .userDetailsService(appUserDetailsService)
            .passwordEncoder(passwordEncoder());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .headers().frameOptions().sameOrigin()
            .and()
                .authorizeRequests()
                .antMatchers(AUTH_WHITELIST).permitAll()
                .antMatchers("/admin/**").hasAuthority(Constants.ROLE_ADMIN)
                .antMatchers("/user/**").hasAuthority(Constants.ROLE_USER)
                .antMatchers("/api/**").authenticated()//individual api will be secured differently
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

    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}

