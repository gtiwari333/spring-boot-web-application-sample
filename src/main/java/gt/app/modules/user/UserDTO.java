package gt.app.modules.user;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class UserDTO {

    private String login;

    private String firstName;

    private String lastName;

    private List<String> authorities;
}
