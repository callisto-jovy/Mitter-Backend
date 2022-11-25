import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ChatManager {
    private final List<Chat> allChats;

    public ChatManager() {
        this.allChats = new ArrayList<>();
    }

    public boolean addAllChat(Chat chat) {
        return this.allChats.add(chat);
    }

    public boolean doesChatBetweenUsersExist(final ClientProfile profile, final ClientProfile profile2) {
        return allChats.stream().anyMatch(c -> c.containsUsers(profile, profile2));
    }

    public Optional<Chat> chatBetweenUsersExist(final ClientProfile profile, final ClientProfile profile2) {
        return allChats.stream().filter(c -> c.containsUsers(profile, profile2)).findFirst();
    }

    public Chat addChat(final ClientProfile u1, final ClientProfile u2) {
        final Chat chat = new Chat(u1.getTag(), u2.getTag());
        allChats.add(chat);
        return chat;
    }

    public void appendToChat(final Chat chat, final ClientProfile sender) {
        //TODO: Append message
    }
}
