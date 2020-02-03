package gt.app.web.rest;

import gt.app.config.security.SecurityUtils;
import gt.app.modules.subscription.SubscriptionDTO;
import gt.app.modules.subscription.SubscriptionService;
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
@RequestMapping("/api/subscription")
@RequiredArgsConstructor
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    @GetMapping("/is-subscribing/{topicId}")
    public ResponseEntity<Boolean> isSubscribing(@PathVariable @NotNull Long topicId) {
        return ok(subscriptionService.isSubscribed(SecurityUtils.getCurrentUserId(), topicId));
    }

    @GetMapping({"/", ""})
    public ResponseEntity<List<SubscriptionDTO>> getAllSubscriptions(@PageableDefault(value = 30) Pageable pageable) {
        Page<SubscriptionDTO> page = subscriptionService.findAllBySubscriberId(SecurityUtils.getCurrentUserId(), pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/subscription/topic");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    @PutMapping("/{topicId}")
    public ResponseEntity<Void> addSubscription(@PathVariable @NotNull Long topicId) {
        subscriptionService.add(SecurityUtils.getCurrentUserId(), topicId);
        return ResponseEntity.ok().headers(HeaderUtil.alertMsg("Subscription Added")).build();
    }

    @DeleteMapping("/{topicId}")
    public ResponseEntity<Void> deleteSubscription(@PathVariable @NotNull Long topicId) {
        subscriptionService.delete(SecurityUtils.getCurrentUserId(), topicId);
        return ResponseEntity.ok().headers(HeaderUtil.deleted("Subscription")).build();
    }

}
