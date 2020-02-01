package gt.app.web.rest;

import gt.app.config.security.SecurityUtils;
import gt.app.modules.follow.FollowDTO;
import gt.app.modules.follow.FollowService;
import gt.app.web.util.HeaderUtil;
import gt.app.web.util.PaginationUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.List;

import static org.springframework.http.ResponseEntity.ok;

@RestController
@RequestMapping("/api/follow")
@RequiredArgsConstructor
public class FollowController {

    private final FollowService followService;

    @GetMapping("/my-followers")
    public ResponseEntity<List<FollowDTO>> getMyFollowers(@PageableDefault(value = 30) Pageable pageable) {
        Page<FollowDTO> page = followService.findFollowerForUser(SecurityUtils.getCurrentUserId(), pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/follow/my-followers");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    @GetMapping("/me-following")
    public ResponseEntity<List<FollowDTO>> getWhomIamFollowing(@PageableDefault(value = 30) Pageable pageable) {
        Page<FollowDTO> page = followService.findFollowingForUser(SecurityUtils.getCurrentUserId(), pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/follow/me-following");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    @GetMapping("/is-following/{authorId}")
    public ResponseEntity<Boolean> isFollowing(@PathVariable @NotNull Long authorId) {
        return ok(followService.isFollowing(authorId, SecurityUtils.getCurrentUserId()));
    }

    @PutMapping("/follow/{toFollow}")
    public ResponseEntity<?> follow(@PathVariable @NotNull Long toFollow) {
        followService.follow(toFollow, SecurityUtils.getCurrentUserId());
        return ok().headers(HeaderUtil.alertMsg("Started Following")).build();

    }

    @DeleteMapping("/unfollow/{followingUserId}")
    public ResponseEntity<?> unfollow(@PathVariable @NotNull Long followingUserId) {
        followService.unfollow(followingUserId, SecurityUtils.getCurrentUserId());
        return ok().headers(HeaderUtil.alertMsg("Stopped Following")).build();
    }

    @DeleteMapping("/delete/{followerId}")
    public ResponseEntity<?> deleteFollower(@PathVariable @NotNull Long followerId) {
        followService.unfollow(SecurityUtils.getCurrentUserId(), followerId);
        return ok().headers(HeaderUtil.alertMsg("Removed follower")).build();
    }

}
