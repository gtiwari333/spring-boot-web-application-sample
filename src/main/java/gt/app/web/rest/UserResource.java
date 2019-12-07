package gt.app.web.rest;

import gt.app.modules.user.UserDTO;
import gt.app.exception.InternalServerErrorException;
import gt.app.modules.user.UserMapper;
import gt.app.modules.user.UserService;
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
            .map(UserMapper.INSTANCE::userToUserDto)
            .orElseThrow(() -> new InternalServerErrorException("Could not retrieve user"));
    }
}
