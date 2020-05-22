package gt.app.config.security;

import com.fasterxml.jackson.annotation.JsonIgnore;
import gt.app.config.Constants;
import gt.app.domain.User;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.UUID;
import java.util.stream.Collectors;

@Getter
public class AppUserDetails {

    private final User user;

    private final Collection<GrantedAuthority> authorities;

    AppUserDetails(User user, Collection<GrantedAuthority> authorities) {
        this.user = user;
        this.authorities = authorities;
    }

    public UUID getUserId() {
        return getUser().getId();
    }

    public String getUsername() {
        return getUser().getUsername();
    }

    @JsonIgnore
    public boolean isUser() {
        return getGrantedAuthorities().contains(Constants.ROLE_USER);
    }

    @JsonIgnore
    public boolean isSystemAdmin() {
        return getGrantedAuthorities().contains(Constants.ROLE_ADMIN);
    }

    public Collection<String> getGrantedAuthorities() {
        Collection<GrantedAuthority> authorities = getAuthorities();
        return authorities.stream().map(GrantedAuthority::getAuthority).collect(Collectors.toSet());
    }

    @JsonIgnore
    public Collection<GrantedAuthority> getAuthorities() {
        return authorities;
    }
}
