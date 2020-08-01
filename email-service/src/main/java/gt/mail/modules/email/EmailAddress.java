package gt.mail.modules.email;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Slf4j
@EqualsAndHashCode
public class EmailAddress implements Serializable {
    private String email;
    private String name;

    public static EmailAddress from(String email, String name) {
        return new EmailAddress(email, name);
    }
}
