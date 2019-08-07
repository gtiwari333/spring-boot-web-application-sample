package g.t.app.web.rest;

import g.t.app.dto.user.UserDTO;
import g.t.app.exception.InternalServerErrorException;
import g.t.app.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
public class UserResource {

    private final UserService userService;


    @GetMapping("/account")
    public UserDTO getAccount() {
        return userService.getCurrentUserWithAuthorities()
            .map(UserDTO::new)
            .orElseThrow(() -> new InternalServerErrorException("Not logged in"));
    }
}
