package gt.app.modules.review;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ReviewStatusHandler {

    final SimpMessagingTemplate messagingTemplate;

    @SendToUser
    public void handle(String userName, String message) {

        messagingTemplate.convertAndSend("/topic/review-results", message);
    }

}
