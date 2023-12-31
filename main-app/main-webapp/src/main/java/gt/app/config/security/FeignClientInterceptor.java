package gt.app.config.security;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

@Component
class FeignClientInterceptor implements RequestInterceptor {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER = "Bearer";

    @Override
    public void apply(RequestTemplate template) {
        Optional<OidcIdToken> idTokenOpt = SecurityUtils.getCurrentUserJWT();
        if (idTokenOpt.isEmpty()) {
            return;
        }

        //get new token if its about to expire
        if (idTokenOpt.get().getExpiresAt().isAfter(Instant.now().plus(20, ChronoUnit.SECONDS))) {
          //TODO: refresh token
        }


        template.header(AUTHORIZATION_HEADER, String.format("%s %s", BEARER, idTokenOpt.get().getTokenValue()));
    }
}
