package gt.app.modules.user;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
public class UserSignUpDTO {

    byte[] avatar;
    String role;
    @NotNull
    private String firstName;
    @NotNull
    private String lastName;
    private String midName;
    @NotNull
    private LocalDate dateOfBirth;
    @NotNull
    private String uniqueId;
}
