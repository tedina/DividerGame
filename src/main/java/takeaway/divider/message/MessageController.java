package takeaway.divider.message;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Controller;
import takeaway.divider.component.GameManager;
import takeaway.divider.model.Message;

import java.util.Objects;

/**
 * Created by Teodora.Toncheva on 02.07.2021
 */
@Controller
public class MessageController {

    private static final Logger logger = LoggerFactory.getLogger(MessageController.class);

    @Autowired
    private MessageFactory messageFactory;

    @Autowired
    private GameManager gameManager;

    @MessageMapping("/play.addUser")
    @SendTo("/queue/public")
    public void addUser(
            @Payload
                    Message message, StompHeaderAccessor accessor) throws JsonProcessingException {

        final String gameId = gameManager.getGameId(accessor);
        final String userId = Objects.requireNonNull(accessor.getUser()).getName();
        final String content = gameManager.getInitialContent(gameId);

        messageFactory.sendToUser(userId, message.getType(), content);
        logger.info("gameId: " + gameId + ", userId: " + userId + ", " + content + " " + message.getType().toString());
    }

    @MessageMapping("/play.makeMove")
    @SendTo("/queue/public")
    public void makeMove(
            @Payload
                    Message message, StompHeaderAccessor accessor) throws JsonProcessingException {

        final String gameId = gameManager.getGameId(accessor);
        final String content = gameManager.getContent(gameId, message);

        messageFactory.sendToAll(gameId, message.getType(), content);

        final String userId = Objects.requireNonNull(accessor.getUser()).getName();
        logger.info("gameId: " + gameId + ", userId: " + userId + ", " + content + " " + message.getType().toString());
    }

}
