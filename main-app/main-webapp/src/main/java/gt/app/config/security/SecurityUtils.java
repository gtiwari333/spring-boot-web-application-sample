package gt.app.config.security;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;

import java.util.*;
import java.util.stream.Collectors;

public final class SecurityUtils {

    private SecurityUtils() {
    }

    public static UUID getCurrentUserId() {

        CurrentUserToken user = getCurrentUserDetails();
        if (user == null) {
            return null;
        }

        return user.getUserId();
    }

    public static CurrentUserToken getCurrentUserDetails() {
        SecurityContext ctx = SecurityContextHolder.getContext();
        Authentication authentication = ctx.getAuthentication();
        if (authentication == null) {
            return null;
        }

        if (authentication.getPrincipal() instanceof DefaultOidcUser defaultOidcUser) {
            return mapAuthenticationPrincipalToCurrentUser(defaultOidcUser);
        }
        return null;
    }

    public static Optional<OidcIdToken> getCurrentUserJWT() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        return Optional
            .ofNullable(securityContext.getAuthentication())
            .filter(auth -> auth.getPrincipal() instanceof DefaultOidcUser)
            .map(auth -> (DefaultOidcUser) auth.getPrincipal())
            .map(DefaultOidcUser::getIdToken);
    }

    public static boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return !(authentication == null || authentication instanceof AnonymousAuthenticationToken);
    }

    public static List<GrantedAuthority> mapRolesToGrantedAuthorities(Collection<String> roles) {
        if (roles == null) {
            return Collections.emptyList();
        }
        return roles.stream()
            .filter(role -> role.startsWith("ROLE_"))
            .map(SimpleGrantedAuthority::new).collect(Collectors.toList());
    }

    public static List<GrantedAuthority> extractAuthorityFromClaims(Map<String, Object> claims) {
        return mapRolesToGrantedAuthorities((ArrayList) claims.get("roles"));
    }

    public static CurrentUserToken mapAuthenticationPrincipalToCurrentUser(DefaultOidcUser oidcUser) {
        return new CurrentUserToken(oidcUser.getAuthorities(), oidcUser.getIdToken(), oidcUser.getUserInfo());
    }
}
