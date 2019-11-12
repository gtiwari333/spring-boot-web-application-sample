package gt.app.web.rest;

import gt.app.dto.user.UserDTO;
import gt.app.exception.InternalServerErrorException;
import gt.app.mapper.UserMapper;
import gt.app.service.UserService;
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
