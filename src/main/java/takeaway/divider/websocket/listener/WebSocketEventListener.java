package takeaway.divider.websocket.listener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import takeaway.divider.component.GameManager;
import takeaway.divider.message.MessageFactory;
import takeaway.divider.model.Message;

/**
 * Created by Teodora.Toncheva on 01.07.2021
 */
@Component
public class WebSocketEventListener {

    @Autowired
    private GameManager gameManager;

    @Autowired
    private MessageFactory messageFactory;

    @EventListener
    public void onSessionConnectEvent(SessionConnectedEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        gameManager.start(accessor);
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        final String gameId = gameManager.getGameId(accessor);
        messageFactory.sendToAll(gameId, Message.MessageType.GAME_INTERRUPTED);
        gameManager.interrupt(accessor);
    }

}