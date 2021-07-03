package takeaway.divider.component;

import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import takeaway.divider.utils.GameUtils;

import java.util.ArrayList;
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
        return startNewGame(accessor);
    }

    private String startNewGame(final StompHeaderAccessor accessor) {
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

    public boolean isTwoPlayers(final String gameId) {
        return game.get(gameId).size() > 1;
    }
}
