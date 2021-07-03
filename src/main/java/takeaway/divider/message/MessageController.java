package takeaway.divider.message;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Controller;
import takeaway.divider.component.GameAttributes;
import takeaway.divider.component.GameManager;
import takeaway.divider.model.Message;
import takeaway.divider.utils.GameUtils;

import java.util.Arrays;
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
        final String content = getInitialContent(gameId);

        messageFactory.sendToUser(userId, message.getType(), content);
        logger.info("gameId: " + gameId + ", userId: " + userId + ", " + content + " " + message.getType().toString());
    }

    @MessageMapping("/play.makeMove")
    @SendTo("/queue/public")
    public void makeMove(
            @Payload
                    Message message, StompHeaderAccessor accessor) throws JsonProcessingException {

        final String gameId = gameManager.getGameId(accessor);
        final String content = getContent(gameId, message);

        messageFactory.sendToAll(gameId, message.getType(), content);

        final String userId = Objects.requireNonNull(accessor.getUser()).getName();
        logger.info("gameId: " + gameId + ", userId: " + userId + ", " + content + " " + message.getType().toString());
    }

    private String getContent(final String gameId, final Message message) throws JsonProcessingException {
        final ObjectMapper mapper = new ObjectMapper();
        final String messageContent = message.getContent();

        final GameAttributes messageAttributes = mapper.readValue(messageContent, GameAttributes.class);
        final GameAttributes gameAttributes = gameManager.getGameAttributesMap().get(gameId);
        updateAttributes(gameId, message, messageAttributes, gameAttributes);


        return mapper.writeValueAsString(gameAttributes);
    }

    private void updateAttributes(String gameId, Message message, GameAttributes messageAttributes,
                                  GameAttributes gameAttributes) {
        final Integer divider = messageAttributes.getDivider();
        gameAttributes.setDivider(divider);

        Integer[] numbers = GameUtils.setOfNumbers(messageAttributes.getDivider());
        gameAttributes.setNumbers(Arrays.asList(numbers));
        boolean canBeDivided = true;

        if (gameAttributes.getResult() == null) {
            //TODO gameAttributes.setResult(GameUtils.generateRandom());
            gameAttributes.setResult(2203);
        } else {

            final Integer number = messageAttributes.getNumber();
            gameAttributes.setNumber(number);

            int currResult = gameAttributes.getResult();
            Message.MessageType type = message.getType();
            if (number != null) {
                canBeDivided = (currResult + number) % divider == 0;
                int result = (currResult + number) / divider;
                gameAttributes.setResult(result);
                if (result == 1) {
                    type = Message.MessageType.WIN;
                } else if (!canBeDivided) {
                    type = Message.MessageType.GAME_OVER;
                }
            }
            message.setType(type);
        }
        gameManager.getGameAttributesMap().put(gameId, gameAttributes);
    }

    private String getInitialContent(final String gameId) throws JsonProcessingException {
        GameAttributes gameAttributes;
        if (gameManager.getGameAttributesMap().containsKey(gameId)) {
            gameAttributes = gameManager.getGameAttributesMap().get(gameId);
            gameAttributes.setHasTwoPlayers(true);
        } else {
            gameAttributes = new GameAttributes();
            gameAttributes.setHasTwoPlayers(false);
            gameManager.getGameAttributesMap().put(gameId, gameAttributes);
        }
        final ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(gameAttributes);
    }
}
