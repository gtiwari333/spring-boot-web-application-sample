package gt.app.modules.user.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record PasswordUpdateDTO(@NotNull @Size(min = 5, max = 50) String pwdPlainText) {
    public static PasswordUpdateDTO of() {
        return new PasswordUpdateDTO("");
    }
}
