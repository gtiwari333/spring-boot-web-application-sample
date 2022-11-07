package gt.app.modules.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserProfileUpdateDTO {

    @Email
    private String email;

    @NotNull
    @Size(min = 2, max = 30)
    private String firstName;

    @Size(max = 30)
    private String lastName;
}
