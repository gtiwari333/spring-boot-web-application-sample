package gt.app.modules.user;

import gt.app.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

interface UserRepository extends JpaRepository<User, UUID> {

    @Override
    boolean existsById(UUID id);
}
