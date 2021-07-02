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
public class ApplicationManager {

    /* Manages games and player objects */
    private final Map<String, List<String>> gameTracker = Collections.synchronizedMap(new LinkedHashMap<>());

    private String lastGameId;

    public Map<String, List<String>> getGameTracker() {
        return gameTracker;
    }

    public String start(StompHeaderAccessor accessor) {
        return startNewGame(accessor);
    }

    private String startNewGame(final StompHeaderAccessor accessor) {
        final String playerId = Objects.requireNonNull(accessor.getUser()).getName();
        String gameId;
        if (lastGameId == null) {
            gameId = GameUtils.generateUUId();
            gameTracker.put(gameId, new ArrayList<String>() {
                {
                    add(playerId);
                }
            });
            lastGameId = gameId;
        } else {
            gameId = lastGameId;
            gameTracker.get(gameId).add(playerId);
            lastGameId = null;
        }
        return gameId;
    }

    public void interrupt(StompHeaderAccessor accessor) {
        final String playerId = Objects.requireNonNull(accessor.getUser()).getName();
        for (Map.Entry<String, List<String>> game : gameTracker.entrySet()) {
            if (game.getValue().contains(playerId)) {
                final String gameId = game.getKey();
                gameTracker.remove(gameId);
                if (lastGameId != null && lastGameId.equals(gameId)) {
                    if (gameTracker.size() >= 1) {
                        Set<Map.Entry<String, List<String>>> entrySet = gameTracker.entrySet();
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
        for (Map.Entry<String, List<String>> game : gameTracker.entrySet()) {
            if (game.getValue().contains(playerId)) {
                return game.getKey();
            }
        }
        throw new RuntimeException(String.format("Game for player %s does not exist!", playerId));
    }

    public boolean isTwoPlayers(final String gameId){
        return gameTracker.get(gameId).size() == 2;
    }
}
