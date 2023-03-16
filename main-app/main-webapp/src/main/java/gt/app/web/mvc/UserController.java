package gt.app.web.mvc;

import gt.app.config.security.AppUserDetails;
import gt.app.modules.user.PasswordUpdateValidator;
import gt.app.modules.user.UserService;
import gt.app.modules.user.UserSignupValidator;
import gt.app.modules.user.dto.PasswordUpdateDTO;
import gt.app.modules.user.dto.UserProfileUpdateDTO;
import gt.app.modules.user.dto.UserSignUpDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserSignupValidator userSignupValidator;
    private final PasswordUpdateValidator passwordUpdateValidator;

    @GetMapping(value = "/signup")
    public String register(Model model) {
        model.addAttribute("user", new UserSignUpDTO());
        return "user/signup";
    }

    @PostMapping(value = "/signup")
    public String register(@Valid @ModelAttribute("user") UserSignUpDTO user, BindingResult bindingResult,
                           RedirectAttributes redirectAttrs) {

        //do custom validation(MVC style) along with the BeanValidation
        userSignupValidator.validate(user, bindingResult);

        if (bindingResult.hasErrors()) {
            return "user/signup";
        }

        userService.create(user);

        redirectAttrs.addFlashAttribute("success", "User " + user.getUsername() + " is created");

        return "redirect:/";
    }

    @GetMapping(value = "/profile")
    public String updateProfile(Model model, @AuthenticationPrincipal AppUserDetails loggedInUserDtl) {
        model.addAttribute("user", new UserProfileUpdateDTO(loggedInUserDtl.getEmail(), loggedInUserDtl.getFirstName(), loggedInUserDtl.getLastName()));
        return "user/profile";
    }

    @PostMapping(value = "/profile")
    public String updateProfile(@Valid @ModelAttribute("user") UserProfileUpdateDTO user, BindingResult bindingResult,
                                @AuthenticationPrincipal AppUserDetails loggedInUserDtl, RedirectAttributes redirectAttrs) {

        if (bindingResult.hasErrors()) {
            return "user/profile";
        }

        userService.update(user, loggedInUserDtl);

        redirectAttrs.addFlashAttribute("success", "Profile updated successfully");

        return "redirect:/"; //self
    }

    @GetMapping(value = "/password")
    public String updatePassword(Model model) {
        model.addAttribute("user", PasswordUpdateDTO.of());
        return "user/password";
    }

    @PostMapping(value = "/password")
    public String updatePassword(@Valid @ModelAttribute("user") PasswordUpdateDTO reqDto, BindingResult bindingResult,
                                 @AuthenticationPrincipal AppUserDetails loggedInUserDtl, RedirectAttributes redirectAttrs) {

        //do custom validation along with the BeanValidation
        passwordUpdateValidator.validate(reqDto, bindingResult, loggedInUserDtl);

        if (bindingResult.hasErrors()) {
            return "user/password";
        }

        userService.updatePassword(reqDto, loggedInUserDtl);

        redirectAttrs.addFlashAttribute("success", "Password updated successfully");

        return "redirect:/";
    }

}
