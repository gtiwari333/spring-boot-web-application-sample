package g.t.app.dto.user;

import g.t.app.domain.User;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;

import java.util.List;
import java.util.stream.Collectors;

@Data
public class UserDTO {

    private String login;

    private String firstName;

    private String lastName;

    private List<String> authorities;

    public UserDTO() {
    }

    public UserDTO(User user) {
        this.login = user.getEmail();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.authorities = user.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList());
    }

}
