package gt.app.modules.user;

import gt.app.domain.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
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

    @Caching(
        evict = {
            @CacheEvict(cacheNames = {"userExistsById"}, key = "#user.id"),
            @CacheEvict(cacheNames = {"userById"}, key = "#user.id")
        }
    )
    public void updateUserIfNeeded(User user) {
        if (!userRepository.existsById(user.getId())) {
            save(user);
        }
    }

    public User getReference(UUID id) {
        return userRepository.getById(id);
    }

    @Cacheable(cacheNames = {"userById"}, key = "#id")
    public User find(UUID id) {
        return userRepository.findById(id).orElseThrow();
    }
}
