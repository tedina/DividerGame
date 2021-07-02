package takeaway.divider.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;
import takeaway.divider.component.ApplicationManager;
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
    private ApplicationManager applicationManager;


    @MessageMapping("/play.sendMessage")
    @SendTo("/queue/public")
    @MessageExceptionHandler
    public void send(final String gameId, final Message.MessageType type, final String content) {
        try {
            for (String user : applicationManager.getGameTracker().get(gameId)) {
                Map<String, String> data = new HashMap<>();
                data.put("content", content);
                data.put("sender", user);
                data.put("type", type.name());

                final String payload;

                payload = new ObjectMapper().writeValueAsString(data);
                final Message message = new Message();
                message.setContent(payload);
                message.setType(type);

                messagingTemplate.convertAndSendToUser(user, "/queue/public", message);
            }
        } catch (JsonProcessingException e) {
            logger.error(e.getMessage());
        }
    }

    public void send(final String gameId, final Message.MessageType type) {
        send(gameId, type, "");
    }
}

