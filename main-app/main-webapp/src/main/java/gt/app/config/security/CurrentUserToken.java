package gt.app.config.security;

import com.fasterxml.jackson.annotation.JsonIgnore;
import gt.app.config.Constants;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;

import java.util.Collection;
import java.util.UUID;
import java.util.stream.Collectors;

@Getter
public class CurrentUserToken extends DefaultOidcUser {

    private final Collection<GrantedAuthority> authorities;
    private final UUID userId;
    private final String username;
    private final String email;

    public CurrentUserToken(Collection<? extends GrantedAuthority> authorities, OidcIdToken idToken, OidcUserInfo userInfo) {
        super(authorities, idToken, userInfo);
        this.authorities = SecurityUtils.extractAuthorityFromClaims(userInfo.getClaims());
        this.userId = UUID.fromString(getName());
        this.username = getAttribute("preferred_username");
        this.email = getAttribute("email");
    }

    public CurrentUserToken(Collection<GrantedAuthority> authorities, String username) {
        super(authorities, OidcIdToken.withTokenValue("DUMMY-TEST").claim("DUMMY", "DUMMY").claim("sub", username).build());
        this.authorities = authorities;
        this.userId = null;
        this.username = username;
        this.email = null;
    }

    @JsonIgnore
    public boolean isUser() {
        return getGrantedAuthorities().contains(Constants.ROLE_USER);
    }

    @JsonIgnore
    public boolean isAdmin() {
        return getGrantedAuthorities().contains(Constants.ROLE_ADMIN);
    }

    @JsonIgnore
    public Collection<String> getGrantedAuthorities() {
        Collection<GrantedAuthority> authorities = getAuthorities();
        return authorities.stream().map(GrantedAuthority::getAuthority).collect(Collectors.toSet());
    }

    @JsonIgnore
    public Collection<GrantedAuthority> getAuthorities() {
        return authorities;
    }

    public UserToken getUserToken() {
        return new UserToken(getUsername(), getIdToken().getTokenValue(), getGrantedAuthorities());
    }

    public record UserToken(String username, String token, Collection<String> roles) {
    }

}
