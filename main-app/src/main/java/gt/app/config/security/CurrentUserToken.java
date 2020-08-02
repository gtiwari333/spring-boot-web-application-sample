package gt.app.config.security;

import org.keycloak.adapters.spi.KeycloakAccount;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.UUID;

public class CurrentUserToken extends KeycloakAuthenticationToken { //this is our principal object

    private AppUserDetails user;

    public CurrentUserToken(KeycloakAccount account, boolean interactive, Collection<? extends GrantedAuthority> authorities) {
        super(account, interactive, authorities);
    }

    public CurrentUserToken(KeycloakAccount account, boolean interactive, Collection<? extends GrantedAuthority> authorities, AppUserDetails user) {
        this(account, interactive, authorities);
        this.user = user;
    }

    public AppUserDetails getUser() {
        return user;
    }

    public UUID getUserId() {
        return user.getUserId();
    }


    public String getUsername() {
        return user.getUsername();
    }

}
