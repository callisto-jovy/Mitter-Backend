import java.util.ArrayList;
import java.util.List;

public class Chat {
    private final String user1;
    private final String user2;
    public List<ChatMessage> messages = new ArrayList<>();

    public Chat(final String pUser1, final String pUser2) {
        user1 = pUser1;
        user2 = pUser2;
    }

    public void appendMessage(final String sender, final String message) {
        messages.add(new ChatMessage(sender, message));
    }

    public void appendMessage(final ChatMessage message) {
        messages.add(message);
    }

    public boolean containsUsers(ClientProfile u1, ClientProfile u2) {
        return (u1.getTag().equals(user1) && u2.getTag().equals(user2)) || (u1.getTag().equals(user2) && u2.getTag().equals(user1));
    }

    public boolean containsUser(ClientProfile clientProfile) {
        return user1.equals(clientProfile.getTag()) || user2.equals(clientProfile.getTag());
    }

    public String getOtherUser(final ClientProfile clientProfile) {
        return clientProfile.getTag().equals(user1) ? user2 : user1;
    }

    public int getLength() {
        return messages.size();
    }

    public String getUser1() {
        return user1;
    }

    public String getUser2() {
        return user2;
    }
}

