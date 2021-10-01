package gt.app.config.security;

import gt.app.domain.User;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.keycloak.representations.IDToken;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.UUID;

/**
 * Utility class for Spring Security.
 */
public final class SecurityUtils {

    private SecurityUtils() {
    }

    /**
     * @return PK ( ID ) of current id
     */
    public static UUID getCurrentUserId() {

        AppUserDetails user = getCurrentUser();
        if (user == null) {
            return null;
        }

        return user.getUserId();
    }


    public static AppUserDetails getCurrentUser() {
        SecurityContext ctx = SecurityContextHolder.getContext();
        Authentication authentication = ctx.getAuthentication();

        return getAppUserDetails(authentication);
    }

    public static AppUserDetails getAppUserDetails(Authentication authentication) {

        AppUserDetails userDetailsOpt = null;

        if (authentication instanceof CurrentUserToken kcToken) {
            return kcToken.getUser();

        } else if (authentication instanceof KeycloakAuthenticationToken kcToken) {
            IDToken idToken = kcToken.getAccount().getKeycloakSecurityContext().getIdToken();

            String id = idToken.getSubject();
            String userId = idToken.getPreferredUsername();
            String firstName = idToken.getGivenName();
            String lastName = idToken.getFamilyName();
            String email = idToken.getEmail();

            userDetailsOpt = new AppUserDetails(new User(id, userId, firstName, lastName, email), kcToken.getAuthorities());
        }

        return userDetailsOpt;
    }

    public static boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return !(authentication == null || authentication instanceof AnonymousAuthenticationToken);
    }

    public static boolean isCurrentUserHasRole(String authority) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && authentication.getAuthorities().stream().anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals(authority));
    }

}
