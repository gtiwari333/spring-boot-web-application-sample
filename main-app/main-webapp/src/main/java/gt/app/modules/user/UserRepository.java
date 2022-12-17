package gt.app.modules.user;

import gt.app.domain.AppUser;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

interface UserRepository extends JpaRepository<AppUser, Long> {

    @Override
    @Cacheable("userExistsById")
    boolean existsById(Long id);


    @Cacheable(value = "userIdByUsername", unless = "#result == null")
    @Query("select id from AppUser where username = :username")
    Long findIdByUsername(@Param("username") String username);

    @Cacheable(value = "userByUsername", unless = "#result == null")
    @EntityGraph(attributePaths = "role")
    AppUser findByUsername(@Param("username") String username);

    @EntityGraph(attributePaths = {"authorities"})
    @Transactional(readOnly = true)
    Optional<AppUser> findOneWithAuthoritiesByUsername(String username);

    @Transactional(readOnly = true)
    boolean existsByUsername(String username);

    Optional<AppUser> findByIdAndActiveIsTrue(Long id);
}
