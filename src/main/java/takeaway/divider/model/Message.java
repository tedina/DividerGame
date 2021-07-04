package takeaway.divider.model;

/**
 * Created by Teodora.Toncheva on 02.07.2021
 */
public class Message {
 
    private MessageType type;

    private String content;

    public enum MessageType {
        START_GAME,
        MAKE_MOVE,
        GAME_OVER,
        GAME_INTERRUPTED,
        WIN
    }

    public MessageType getType() {
        return type;
    }

    public void setType(MessageType type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
