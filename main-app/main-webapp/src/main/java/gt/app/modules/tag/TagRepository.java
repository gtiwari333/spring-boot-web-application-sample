package gt.app.modules.tag;

import gt.app.domain.Tag;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TagRepository extends JpaRepository<Tag, String> {

    @Cacheable("tagExistsById")
    boolean existsByName(String name);

}
