package g.t.app.service;

import g.t.app.config.security.SecurityUtils;
import g.t.app.domain.User;
import g.t.app.dto.user.UserProfileUpdateDTO;
import g.t.app.dto.user.UserSignUpDTO;
import g.t.app.exception.RecordNotFoundException;
import g.t.app.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public Optional<User> getCurrentUserWithAuthorities() {
        return SecurityUtils.getCurrentUserLogin().flatMap(userRepository::findOneWithAuthoritiesByUniqueId);
    }

    public Page<User> findAll(Pageable pageable) {
        return userRepository.findAllByActiveIsTrue(pageable);
    }

    public Optional<User> findById(Long id) {
        return userRepository.findByIdAndActiveIsTrue(id);
    }

    public void update(UserProfileUpdateDTO toUpdate) {
        User author = userRepository.findOneByUniqueId(toUpdate.getUniqueId())
            .orElseThrow(() -> new RecordNotFoundException("User", "login", toUpdate.getUniqueId()));

        BeanUtils.copyProperties(toUpdate, author);

        userRepository.save(author);
    }

    public User create(UserSignUpDTO toCreate) {
        User author = new User();
        BeanUtils.copyProperties(toCreate, author);
        return userRepository.save(author);
    }

    public void delete(Long id) {
        User author = userRepository.findByIdAndActiveIsTrue(id)
            .orElseThrow(() -> new RecordNotFoundException("User", "id", id));

        author.setActive(false);
        userRepository.save(author);
    }

}
