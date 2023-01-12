import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ChatManager {
    private final List<Chat> allChats;
    private final List<ChatMessage> publicChat;

    public ChatManager() {
        this.allChats = new ArrayList<>();
        this.publicChat = new ArrayList<>();
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

    public void appendChat(final Chat chat) {
        allChats.add(chat);
    }

    public List<String> getAllChatPartners(final ClientProfile clientProfile) {
        return allChats
                .stream()
                .filter(chat -> chat.containsUser(clientProfile))
                .map(chat -> chat.getOtherUser(clientProfile))
                .collect(Collectors.toList());
    }

    public List<Chat> getAllChats() {
        return allChats;
    }

    public List<ChatMessage> getPublicChat() {
        return publicChat;
    }
}
