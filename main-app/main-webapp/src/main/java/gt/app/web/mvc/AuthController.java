package gt.app.web.mvc;

import gt.app.config.AppProperties;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Controller
@RequestMapping("/auth")
public class AuthController {

    private final ClientRegistration registration;
    private final AppProperties appProperties;

    public AuthController(ClientRegistrationRepository registrations, AppProperties appProperties) {
        this.registration = registrations.findByRegistrationId("oidc");
        this.appProperties = appProperties;
    }

    /**
     * Redirects to Keycloak's account management page where users can:
     * - Change password
     * - Update profile (email, first name, last name)
     * - Configure 2FA/OTP
     * - Manage connected accounts
     * - View session history
     */
    @GetMapping("/settings")
    public String settings(HttpServletRequest request, HttpServletResponse response,
                           RedirectAttributes redirectAttributes,
                           @AuthenticationPrincipal(expression = "idToken") OidcIdToken idToken) {

        String keycloakAccountUrl = buildKeycloakAccountUrl(request, idToken);

        // Add a success message that will be shown after redirecting back to your app
        redirectAttributes.addFlashAttribute("info", "Redirecting to Keycloak Account Management");

        return "redirect:" + keycloakAccountUrl;
    }

    /**
     * Redirects directly to Keycloak's password change page
     * This takes the user to the specific tab for changing password
     */
    @GetMapping("/change-password")
    public String changePassword(HttpServletRequest request, HttpServletResponse response,
                                 RedirectAttributes redirectAttributes,
                                 @AuthenticationPrincipal(expression = "idToken") OidcIdToken idToken) {

        String keycloakAccountUrl = buildKeycloakAccountUrl(request, idToken);

        // Keycloak's account console hash fragment for password change
        // The "#/security/signingin" fragment is handled client-side by Keycloak
        String passwordChangeUrl = keycloakAccountUrl + "#/security/signingin";

        redirectAttributes.addFlashAttribute("info", "Redirecting to Keycloak Password Change");

        return "redirect:" + passwordChangeUrl;
    }

    /**
     * Alternative approach: Redirect to Keycloak's account console with a specific referrer
     * This can be used if you want to redirect back to a specific page in your app
     */
    @GetMapping("/account")
    public String account(HttpServletRequest request,
                          @AuthenticationPrincipal(expression = "idToken") OidcIdToken idToken) {

        String keycloakAccountUrl = buildKeycloakAccountUrl(request, idToken);

        // Add a redirect back to your application after user finishes in Keycloak
        String referrer = appProperties.getWeb().getBaseUrl();
        String finalUrl = keycloakAccountUrl + "?referrer=" + URLEncoder.encode(referrer, StandardCharsets.UTF_8)
            + "&referrer_uri=" + URLEncoder.encode(referrer, StandardCharsets.UTF_8);

        return "redirect:" + finalUrl;
    }

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
        return "redirect:/oauth2/authorization/oidc";
    }

    /**
     * Builds the Keycloak account console URL with proper redirect handling
     */
    private String buildKeycloakAccountUrl(HttpServletRequest request, OidcIdToken idToken) {
        // Derive from issuer URL (e.g., https://keycloak:8080/realms/your-realm)
        String issuerUri = this.registration.getProviderDetails().getIssuerUri();
        return issuerUri + "/account";
    }

}
