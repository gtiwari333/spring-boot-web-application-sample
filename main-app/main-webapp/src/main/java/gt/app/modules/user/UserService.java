package gt.app.modules.user;

import gt.api.email.EmailDto;
import gt.app.api.EmailClient;
import gt.app.config.Constants;
import gt.app.config.security.AppUserDetails;
import gt.app.domain.AppUser;
import gt.app.domain.LiteUser;
import gt.app.exception.RecordNotFoundException;
import gt.app.modules.user.dto.PasswordUpdateDTO;
import gt.app.modules.user.dto.UserProfileUpdateDTO;
import gt.app.modules.user.dto.UserSignUpDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.Set;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthorityService authorityService;
    private final EmailClient emailService;

    private final LiteUserRepository liteUserRepository;

    public void update(UserProfileUpdateDTO toUpdate, AppUserDetails userDetails) {
        LiteUser user = liteUserRepository.findOneByUsername(userDetails.getUsername())
            .orElseThrow(() -> new RecordNotFoundException("User", "login", userDetails.getUsername()));

        user.setFirstName(toUpdate.getFirstName());
        user.setLastName(toUpdate.getLastName());
        user.setEmail(toUpdate.getEmail());

        liteUserRepository.save(user);
    }

    public void updatePassword(PasswordUpdateDTO toUpdate, AppUserDetails userDetails) {
        LiteUser user = liteUserRepository.findOneByUsername(userDetails.getUsername())
            .orElseThrow(() -> new RecordNotFoundException("User", "login", userDetails.getUsername()));

        user.setPassword(passwordEncoder.encode(toUpdate.pwdPlainText()));
        liteUserRepository.save(user);
    }

    public AppUser create(UserSignUpDTO toCreate) {

        var user = new AppUser(toCreate.getUsername(), toCreate.getFirstName(), toCreate.getLastName(), toCreate.getEmail());

        user.setPassword(passwordEncoder.encode(toCreate.getPwdPlaintext()));

        user.setAuthorities(authorityService.findByNameIn(Constants.ROLE_USER));

        userRepository.save(user);

        EmailDto dto = EmailDto.of("system@noteapp", Set.of(user.getEmail()),
            "NoteApp Account Created!",
            "Thanks for signing up.");

        emailService.sendEmailWithAttachments(dto);

        return user;
    }

    public void delete(Long id) {
        LiteUser author = liteUserRepository.findByIdAndActiveIsTrue(id)
            .orElseThrow(() -> new RecordNotFoundException("User", "id", id));

        author.setActive(Boolean.FALSE);
        liteUserRepository.save(author);
    }

    public AppUser save(AppUser u) {
        return userRepository.save(u);
    }

    public boolean exists(Long id) {
        return userRepository.existsById(id);
    }

    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    public void updateUserIfNeeded(AppUser user) {
        if (!userRepository.existsById(user.getId())) {
            save(user);
        }
    }

    public AppUser getReference(Long id) {
        return userRepository.getReferenceById(id);
    }

    public Optional<AppUser> findIdByUserLogin(Long id) {
        return userRepository.findById(id);
    }

    public Long findIdByUserLogin(String userLogin) {
        return userRepository.findIdByUsername(userLogin);
    }

    public AppUser findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public Optional<AppUser> findById(Long id) {
        return userRepository.findById(id);
    }

    public String userSummary() {
        return "";
    }
}
