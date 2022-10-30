package gt.app.web.mvc;

import lombok.RequiredArgsConstructor;
import org.keycloak.adapters.AdapterDeploymentContext;
import org.keycloak.adapters.KeycloakDeployment;
import org.keycloak.adapters.springsecurity.facade.SimpleHttpFacade;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    final AdapterDeploymentContext adapter;

    @GetMapping("/change-password")
    public String changePwd(RedirectAttributes redirectAttributes, HttpServletRequest req, HttpServletResponse resp) {
        return getKeyCloakAccountUrl(redirectAttributes, req, resp);
    }

    @GetMapping("/settings")
    public String settings(RedirectAttributes redirectAttributes, HttpServletRequest req, HttpServletResponse resp) {
        return getKeyCloakAccountUrl(redirectAttributes, req, resp);
    }

    String getKeyCloakAccountUrl(RedirectAttributes redirectAttributes, HttpServletRequest req, HttpServletResponse resp) {
        /*
        KeycloakDeployment deployment = adapter.resolveDeployment(new SimpleHttpFacade(req, resp));

        redirectAttributes.addAttribute("referrer", deployment.getResourceName());

        return "redirect:" + deployment.getAccountUrl(); */
        return "KEYCLOAK DOESN'T SUPPORT JAKARTA namespace yet";
    }
}
