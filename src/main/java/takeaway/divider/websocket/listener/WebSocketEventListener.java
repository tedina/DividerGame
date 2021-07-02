package takeaway.divider.websocket.listener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import takeaway.divider.component.ApplicationManager;
import takeaway.divider.controller.MessageFactory;
import takeaway.divider.model.Message;

/**
 * Created by Teodora.Toncheva on 01.07.2021
 */
@Component
public class WebSocketEventListener {

    @Autowired
    private ApplicationManager applicationManager;

    @Autowired
    private MessageFactory messageFactory;

    @EventListener
    public void onSessionConnectEvent(SessionConnectedEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        applicationManager.start(accessor);
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        final String gameId = applicationManager.getGameId(accessor);
        messageFactory.send(gameId, Message.MessageType.GAME_INTERRUPTED);
        applicationManager.interrupt(accessor);
    }

}