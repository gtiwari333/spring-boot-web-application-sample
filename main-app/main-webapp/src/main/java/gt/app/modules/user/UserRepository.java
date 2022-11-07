package gt.app.modules.user;

import gt.app.domain.User;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

interface UserRepository extends JpaRepository<User, UUID> {

    @Override
    @Cacheable("userExistsById")
    boolean existsById(UUID id);


    @Cacheable(value = "userIdByUsername", unless = "#result == null")
    @Query("select id from User where username = :username")
    UUID findIdByUsername(@Param("username") String username);

    @Cacheable(value = "userByUsername", unless = "#result == null")
    @EntityGraph(attributePaths = "role")
    User findByUsername(@Param("username") String username);
}
