package g.t.app.web.mvc;

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

    @GetMapping("/")
    public String index(Model model, Pageable pageable) { //uses default page sizes
        model.addAttribute("greeting", "Hello Spring");
        return "index";
    }

    @GetMapping("/admin")
    public String adminHome(Model model, @AuthenticationPrincipal UserDetails principal, Pageable pageable) {
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
