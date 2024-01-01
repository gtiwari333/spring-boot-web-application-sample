package gt.app.web.mvc;

import gt.app.config.AppProperties;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/auth")
public class AuthController {

    private final ClientRegistration registration;
    private final AppProperties appProperties;

    public AuthController(ClientRegistrationRepository registrations, AppProperties appProperties) {
        this.registration = registrations.findByRegistrationId("oidc");
        this.appProperties = appProperties;
    }

    //FIXME: use keycloak rest api to handle these

//    @GetMapping("/change-password")
//    public String changePwd(RedirectAttributes redirectAttributes, HttpServletRequest req, HttpServletResponse resp) {
//        return getKeyCloakAccountUrl(redirectAttributes, req, resp) + "/#/security/signingin";
//    }
//
//    @GetMapping("/settings")
//    public String settings(RedirectAttributes redirectAttributes, HttpServletRequest req, HttpServletResponse resp) {
//        return getKeyCloakAccountUrl(redirectAttributes, req, resp);
//    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request, @AuthenticationPrincipal(expression = "idToken") OidcIdToken idToken) {
        var logoutUrlSb = new StringBuilder();
        logoutUrlSb.append(this.registration.getProviderDetails().getConfigurationMetadata().get("end_session_endpoint").toString());
        String originUrl = request.getHeader(HttpHeaders.ORIGIN);
        if (!StringUtils.hasText(originUrl)) {
            originUrl = appProperties.getWeb().getBaseUrl() + "?logout=true";
        }
        logoutUrlSb.append("?id_token_hint=").append(idToken.getTokenValue()).append("&post_logout_redirect_uri=").append(originUrl);

        request.getSession().invalidate();
        return "redirect:" + logoutUrlSb;
    }

    @GetMapping("/login")
    public String login() {
        return "redirect:/oauth2/authorization/oidc"; //this will redirect to login page
    }
}
