package takeaway.divider.model;

/**
 * Created by Teodora.Toncheva on 02.07.2021
 */
public class Message {

    private MessageType type;

    private String content;

    private String sender;

    public enum MessageType {
        START_GAME,
        GAME_STARTED,
        GAME_INTERRUPTED
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

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }
}
