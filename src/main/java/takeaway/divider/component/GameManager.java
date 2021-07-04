package takeaway.divider.component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import takeaway.divider.model.Message;
import takeaway.divider.utils.GameUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * Created by Teodora.Toncheva on 01.07.2021
 */
@Component
public class GameManager {

    /* Manages games and player objects */
    private final Map<String, List<String>> game = Collections.synchronizedMap(new LinkedHashMap<>());
    private final Map<String, GameAttributes> gameAttributesMap = Collections.synchronizedMap(new LinkedHashMap<>());

    private String lastGameId;

    public Map<String, List<String>> getGame() {
        return game;
    }

    public Map<String, GameAttributes> getGameAttributesMap() {
        return gameAttributesMap;
    }

    public String start(StompHeaderAccessor accessor) {
        final String playerId = Objects.requireNonNull(accessor.getUser()).getName();
        String gameId;
        if (lastGameId == null) {
            gameId = GameUtils.generateUUId();
            game.put(gameId, new ArrayList<String>() {
                {
                    add(playerId);
                }
            });
            lastGameId = gameId;
        } else {
            gameId = lastGameId;
            game.get(gameId).add(playerId);
            lastGameId = null;
        }
        return gameId;
    }

    public void interrupt(StompHeaderAccessor accessor) {
        final String playerId = Objects.requireNonNull(accessor.getUser()).getName();
        for (Map.Entry<String, List<String>> game : game.entrySet()) {
            if (game.getValue().contains(playerId)) {
                final String gameId = game.getKey();
                this.game.remove(gameId);
                if (lastGameId != null && lastGameId.equals(gameId)) {
                    if (this.game.size() >= 1) {
                        Set<Map.Entry<String, List<String>>> entrySet = this.game.entrySet();
                        for (Map.Entry<String, List<String>> stringListEntry : entrySet) {
                            lastGameId = stringListEntry.getKey();
                        }
                    } else {
                        lastGameId = null;
                    }
                }
            }
        }
    }

    public String getGameId(StompHeaderAccessor accessor) {
        final String playerId = Objects.requireNonNull(accessor.getUser()).getName();
        for (Map.Entry<String, List<String>> game : game.entrySet()) {
            if (game.getValue().contains(playerId)) {
                return game.getKey();
            }
        }
        throw new RuntimeException(String.format("Game for player %s does not exist!", playerId));
    }

    public String getInitialContent(final String gameId) throws JsonProcessingException {
        GameAttributes gameAttributes;
        if (getGameAttributesMap().containsKey(gameId)) {
            gameAttributes = getGameAttributesMap().get(gameId);
            gameAttributes.setHasTwoPlayers(true);
        } else {
            gameAttributes = new GameAttributes();
            gameAttributes.setHasTwoPlayers(false);
            getGameAttributesMap().put(gameId, gameAttributes);
        }
        final ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(gameAttributes);
    }

    public String getContent(final String gameId, final Message message) throws JsonProcessingException {
        final ObjectMapper mapper = new ObjectMapper();
        final String messageContent = message.getContent();

        final GameAttributes messageAttributes = mapper.readValue(messageContent, GameAttributes.class);
        final GameAttributes gameAttributes = getGameAttributesMap().get(gameId);
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
            gameAttributes.setResult(GameUtils.generateRandom());
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
        getGameAttributesMap().put(gameId, gameAttributes);
    }
}
