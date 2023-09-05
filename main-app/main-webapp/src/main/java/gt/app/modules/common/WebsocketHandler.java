package gt.app.modules.common;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class WebsocketHandler {

    final SimpMessagingTemplate messagingTemplate;

    @SendToUser
    public void sendToUser(String userName, String message) {
        messagingTemplate.convertAndSendToUser(userName, "/topic/review-results", message);
    }

    @SendTo
    public void sendToAll(String message) {
        messagingTemplate.convertAndSend("/topic/global-messages", message);
    }

}
