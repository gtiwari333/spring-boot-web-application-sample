package gt.app.modules.user;

import gt.app.modules.user.dto.UserSignUpDTO;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@RequiredArgsConstructor
@Component
public class UserSignupValidator implements Validator {

    final UserRepository userRepository;

    @Override
    public boolean supports(Class clazz) {
        return UserSignUpDTO.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        UserSignUpDTO toCreate = (UserSignUpDTO) target;

        if (StringUtils.containsIgnoreCase(toCreate.getPwdPlaintext(), toCreate.getUsername()) || StringUtils.containsIgnoreCase(toCreate.getUsername(), toCreate.getPwdPlaintext())) {
            errors.rejectValue("pwdPlaintext", "user.weakpwd", "Weak password, choose another");
        }

        if (userRepository.existsByUsername(toCreate.getUsername())) {
            errors.rejectValue("username", "user.alreadyexists", "Username " + toCreate.getUsername() + " already exists");
        }

        if (userRepository.existsByEmail(toCreate.getEmail())) {
            errors.rejectValue("email", "email.alreadyexists", "User with email " + toCreate.getEmail() + " already exists");
        }
    }

}
