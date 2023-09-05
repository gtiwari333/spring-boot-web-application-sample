package gt.app.modules.jobs;

import gt.app.modules.common.WebsocketHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.TimeZone;

@Component
@Slf4j
@RequiredArgsConstructor
@Profile("!test")
public class ServerTimeSenderTask {

    final WebsocketHandler websocketHandler;
    static final DateTimeFormatter DT_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss");

    @Scheduled(fixedRate = 30 * 1000L)
    void sendCurrentTimeToAllUsers() {
        websocketHandler.sendToAll("Current Server Time is " + LocalDateTime.now().format(DT_FORMAT) + " (" + TimeZone.getDefault().getID() + ")");
    }
}
