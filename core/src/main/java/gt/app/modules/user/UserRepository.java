package gt.app.modules.user;

import gt.app.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

interface UserRepository extends JpaRepository<User, Long> {

    @EntityGraph(attributePaths = {"authorities"})
    Optional<User> findOneWithAuthoritiesByUniqueId(String uniqueId);

    Optional<User> findOneByUniqueId(String uniqueId);

    Optional<User> findByIdAndActiveIsTrue(Long id);

    Page<User> findAllByActiveIsTrue(Pageable pageable);

    Iterable<User> findByFirstName(String personName);

    Iterable<User> findByFirstNameStartingWith(String emailSuffix);

    Iterable<User> findByEmailEndingWith(String emailSuffix);

    Iterable<User> findByFirstNameAndDateOfBirth(String name, LocalDate dob);

    Iterable<User> findByFirstNameOrEmail(String first, String email);

    Iterable<User> findByFirstNameLikeOrEmail(String firstLike, String email);

    Iterable<User> findByFirstNameNotLike(String firstName);

}
