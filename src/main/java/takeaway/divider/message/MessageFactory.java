package takeaway.divider.message;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;
import takeaway.divider.component.GameManager;
import takeaway.divider.model.Message;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Teodora.Toncheva on 02.07.2021
 */
@Controller
public class MessageFactory {

    private static final Logger logger = LoggerFactory.getLogger(MessageFactory.class);

    @Autowired
    private SimpMessageSendingOperations messagingTemplate;

    @Autowired
    private GameManager applicationManager;

    @SendTo("/queue/public")
    @MessageExceptionHandler
    public void sendToUser(final String userId, final Message.MessageType type, final String content) {
        try {
            initializeAndSend(userId, type, content);
        } catch (JsonProcessingException e) {
            logger.error(e.getMessage());
        }
    }

    @SendTo("/queue/public")
    @MessageExceptionHandler
    public void sendToAll(final String gameId, final Message.MessageType type, final String content) {
        try {
            for (String user : applicationManager.getGame().get(gameId)) {
                initializeAndSend(user, type, content);
            }
        } catch (JsonProcessingException e) {
            logger.error(e.getMessage());
        }
    }

    public void sendToAll(final String gameId, final Message.MessageType type) {
        sendToAll(gameId, type, "");
    }

    private void initializeAndSend(String userId, Message.MessageType type, String content)
            throws JsonProcessingException {
        Map<String, String> data = new HashMap<>();
        data.put("content", content);
        data.put("sender", userId);
        data.put("type", type.name());

        final String payload;
        payload = new ObjectMapper().writeValueAsString(data);

        final Message message = new Message();
        message.setContent(payload);
        message.setType(type);

        messagingTemplate.convertAndSendToUser(userId, "/queue/public", message);
    }
}

