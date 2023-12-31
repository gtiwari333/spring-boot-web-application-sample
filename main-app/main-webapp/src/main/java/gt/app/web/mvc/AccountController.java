package gt.app.web.mvc;

import gt.app.config.security.CurrentUserToken;
import gt.app.modules.user.UserService;
import gt.app.modules.user.UserStat;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@Controller
@RequiredArgsConstructor
class AccountController {
    final UserService userService;

    @GetMapping("/account/user/{id}")
    public String getUserSummary(Model model, @AuthenticationPrincipal CurrentUserToken loggedInUserDtl, @PathVariable UUID id) {


        model.addAttribute("name", "FName LastName");
        model.addAttribute("stat", new UserStat());


        model.addAttribute("isAlreadyFollowing", "false");
        return "_fragments/user :: user-summary ";
    }


}
