package gt.app.modules.user;

import gt.app.domain.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;

    public boolean exists(UUID id) {
        return userRepository.existsById(id);
    }

    public User save(User u) {
        return userRepository.save(u);
    }

    public void updateUserIfNeeded(User user) {
        if (!userRepository.existsById(user.getId())) {
            save(user);
        }
    }

    public User getReference(UUID id) {
        return userRepository.getReferenceById(id);
    }

    public Optional<User> findIdByUserLogin(UUID id) {
        return userRepository.findById(id);
    }

    public UUID findIdByUserLogin(String userLogin) {
        return userRepository.findIdByUsername(userLogin);
    }

    public User findByUserLogin(String userLogin) {
        return userRepository.findByUsername(userLogin);
    }

    public String userSummary() {
        return "";
    }
}
