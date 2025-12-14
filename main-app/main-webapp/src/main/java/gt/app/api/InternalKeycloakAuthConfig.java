package gt.app.api;

import feign.RequestInterceptor;
import gt.app.config.security.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;

import java.util.Optional;

@Slf4j
class InternalKeycloakAuthConfig {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER = "Bearer";


    @Bean
    public RequestInterceptor bearerAuthRequestInterceptor() {
        return template -> {
            //NOTE:   pass the tokens only to the app that expects it

            Optional<OidcIdToken> idTokenOpt = SecurityUtils.getCurrentUserJWT();
            if (idTokenOpt.isEmpty()) {
                return;
            }

            /*
            //get new token if its about to expire
            if (idTokenOpt.get().getExpiresAt().isAfter(Instant.now().plus(20, ChronoUnit.SECONDS))) {
              //TODO: refresh token
            }
            */
            log.info("Calling {} with token", template.url());
            template.header(AUTHORIZATION_HEADER, "%s %s".formatted(BEARER, idTokenOpt.get().getTokenValue()));
        };
    }

}
