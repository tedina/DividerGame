package takeaway.divider.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Controller;
import takeaway.divider.component.ApplicationManager;
import takeaway.divider.model.Message;

/**
 * Created by Teodora.Toncheva on 02.07.2021
 */
@Controller
public class MessageController {

    @Autowired
    private MessageFactory messageFactory;

    @Autowired
    private ApplicationManager applicationManager;

    @MessageMapping("/play.addUser")
    @SendTo("/queue/public")
    public void addUser(
            @Payload
                    Message message, StompHeaderAccessor accessor) {
        final String gameId = applicationManager.getGameId(accessor);
        final String player = applicationManager.isTwoPlayers(gameId) ? "2" : "1";
        messageFactory.send(gameId, message.getType(), player);
    }
}
