package gt.app.modules.user;

import gt.app.modules.user.dto.PasswordUpdateDTO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class PasswordUpdateValidator implements Validator {

    @Override
    public boolean supports(Class clazz) {
        return PasswordUpdateDTO.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
    }

    public void validate(Object target, Errors errors, UserDetails principal) {

        PasswordUpdateDTO toCreate = (PasswordUpdateDTO) target;

        if (StringUtils.containsIgnoreCase(toCreate.pwdPlainText(), principal.getUsername()) || StringUtils.containsIgnoreCase(principal.getUsername(), toCreate.pwdPlainText())) {
            errors.rejectValue("pwdPlaintext", "user.weakpwd", "Weak password, choose another");
        }

    }

}
