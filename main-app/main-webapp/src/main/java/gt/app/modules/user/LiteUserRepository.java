package gt.app.modules.user;

import gt.app.domain.LiteUser;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

interface LiteUserRepository extends JpaRepository<LiteUser, Long> {
    @Transactional(readOnly = true)
    @Cacheable(value = "userByUsername", unless = "#result == null")
    Optional<LiteUser> findOneByUsername(String username);
}
