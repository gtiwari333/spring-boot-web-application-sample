package gt.app.config.security;

import com.fasterxml.jackson.annotation.JsonIgnore;
import gt.app.config.Constants;
import gt.app.domain.Authority;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;

@Getter
public class AppUserDetails extends User {

    private final Long id;
    private final String firstName;

    private final String lastName;
    private final String email;

    public AppUserDetails(Long id, String userName, String email, String password, String firstName, String lastName, Collection<Authority> authorities,
                          boolean enabled, boolean accountNonExpired, boolean credentialsNonExpired, boolean accountNonLocked) {

        super(userName, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);

        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        AppUserDetails that = (AppUserDetails) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), id);
    }
}
