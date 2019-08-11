package g.t.app.web.mvc;

import g.t.app.config.security.SecurityUtils;
import g.t.app.config.security.UserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@Slf4j
@RequiredArgsConstructor
public class IndexController {

    @GetMapping({"/", ""})
    public String index(Model model) {
        model.addAttribute("greeting", "Hello Spring");

        if (SecurityUtils.isAuthenticated()) {

            UserDetails loggedInUser = SecurityUtils.getCurrentUserDetails();

            if (loggedInUser.isSystemAdmin()) {
                return "redirect:admin";
            }

            if (loggedInUser.isOwner()) {
                return "redirect:owner";
            }


            if (loggedInUser.isUser()) {
                return "redirect:user";
            }

            throw new RuntimeException("Unsupported User Type");
        }

        return "index";
    }

    @GetMapping("/admin")
    public String adminHome(Model model, @AuthenticationPrincipal UserDetails principal) {
        model.addAttribute("message", getWelcomeMessage(principal));
        return "home";
    }

    @GetMapping("/user")
    public String userHome(Model model, @AuthenticationPrincipal UserDetails principal) {
        model.addAttribute("message", getWelcomeMessage(principal));
        return "home";
    }

    @GetMapping("/owner")
    public String ownerHome(Model model, @AuthenticationPrincipal UserDetails principal) {
        model.addAttribute("message", getWelcomeMessage(principal));
        return "home";
    }

    private String getWelcomeMessage(UserDetails principal) {
        return "Hello " + principal.getUsername() + ", you have " + String.join(", ", principal.getGrantedAuthorities());
    }

}
