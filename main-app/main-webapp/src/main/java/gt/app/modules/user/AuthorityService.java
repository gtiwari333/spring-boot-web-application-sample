package gt.app.modules.user;

import gt.app.domain.Authority;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthorityService {

    final AuthorityRepository authorityRepository;

    public void save(Authority auth) {
        authorityRepository.save(auth);
    }

    public Set<Authority> findByNameIn(String... roles) {
        return authorityRepository.findByNameIn(List.of(roles));
    }
}
