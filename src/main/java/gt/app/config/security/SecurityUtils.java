package gt.app.config.security;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

/**
 * Utility class for Spring Security.
 */
public final class SecurityUtils {

    private SecurityUtils() {
    }

    /**
     * Get the login of the current user.
     *
     * @return the login of the current user
     */
    public static Optional<String> getCurrentUserLogin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return Optional.ofNullable(authentication)
            .map(a -> {
                if (a.getPrincipal() instanceof UserDetails) {
                    UserDetails springSecurityUser = (UserDetails) a.getPrincipal();
                    return springSecurityUser.getUsername();
                } else if (a.getPrincipal() instanceof String) {
                    return (String) a.getPrincipal();
                }
                return null;
            });
    }

    /**
     * @return PK ( ID ) of current id
     */
    public static Long getCurrentUserId() {

        UserDetails user = getCurrentUserDetails();
        if (user != null) {
            return user.getId();
        }
        return null;
    }

    public static UserDetails getCurrentUserDetails() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return getCurrentUserDetails(authentication);
    }

    public static UserDetails getCurrentUserDetails(Authentication authentication) {
        UserDetails userDetails = null;
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            userDetails = (UserDetails) authentication.getPrincipal();
        }
        return userDetails;
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
