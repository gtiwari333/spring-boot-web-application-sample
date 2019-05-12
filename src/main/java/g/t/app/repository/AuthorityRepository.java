package g.t.app.repository;

import g.t.app.domain.Authority;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Set;

public interface AuthorityRepository extends JpaRepository<Authority, String> {

    Authority findByName(String authority);

    Set<Authority> findByNameIn(String... authorities);
}
