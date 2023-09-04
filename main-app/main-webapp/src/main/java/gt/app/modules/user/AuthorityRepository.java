package gt.app.modules.user;

import gt.app.domain.Authority;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Set;

interface AuthorityRepository extends JpaRepository<Authority, String> {
    @Transactional(readOnly = true)
    Set<Authority> findByNameIn(Collection<String> name);
}
