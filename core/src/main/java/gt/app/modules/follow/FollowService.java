package gt.app.modules.follow;

import gt.app.domain.Follow;
import gt.app.modules.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class FollowService { //candiate for caching

    private final FollowRepository followRepository;
    private final UserService userService;

    public Page<FollowDTO> findFollowerForUser(Long userId, Pageable pageable) {
        return followRepository.getFollowers(userId, pageable);
    }

    public Page<FollowDTO> findFollowingForUser(Long userId, Pageable pageable) {
        return followRepository.getFollowing(userId, pageable);
    }

    public Follow follow(Long userId, Long followerId) {
        Follow follow = new Follow();
        follow.setFollower(userService.getReference(userId));
        follow.setUser(userService.getReference(followerId));
        return followRepository.save(follow);
    }

    public void unfollow(Long userId, Long followerId) {
        followRepository.delete(userId, followerId);
    }

    public boolean isFollowing(Long authorId, Long followerId) {
        return followRepository.existsByUserIdAndFollowerId(authorId, followerId);
    }
}
