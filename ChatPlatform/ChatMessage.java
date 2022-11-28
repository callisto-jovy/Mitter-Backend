public class ChatMessage {

    public ChatMessage(String sender, String message) {
        this.sender = sender;
        this.message = message;
    }

    private final String sender;
    private final String message;

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "ChatMessage{" +
                "sender='" + sender + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}
