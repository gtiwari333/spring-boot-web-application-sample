package g.t.app.dto.user;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
public class UserSignUpDTO {

    @NotNull
    private String firstName;

    @NotNull
    private String lastName;

    private String midName;

    @NotNull
    private LocalDate dateOfBirth;

    @NotNull
    private String uniqueId;

    byte[] avatar;

    String role;
}
