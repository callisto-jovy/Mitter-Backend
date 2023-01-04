import java.util.ArrayList;
import java.util.Optional;
import java.util.function.BiConsumer;

public class ChatHandler implements Constant {

    public static void handleChat(final EncoderUtil enc, final String ip, final int port, final BiConsumer<String, String> receiverMessageConsumer) {
        //Check whether the sender is even online
        if (!USER_MANAGER.isUserOnline(ip, port)) {
            receiverMessageConsumer.accept(null, enc.format(ErrorType.ACCOUNT_NOT_LOGGED_IN));
            return;
        }
        if (enc.getOperation().equals("WRT")) {
            //Given arguments 0 - receiver, 1 - message
            final String receiver = enc.getArgument(0);
            final String message = enc.getArgument(1);

            final Optional<ClientProfile> receiverProfileOptional = USER_MANAGER.getUser(receiver);
            final Optional<ClientProfile> senderProfileOptional = USER_MANAGER.getOnlineUser(ip, port);

            if (receiverProfileOptional.isPresent() && senderProfileOptional.isPresent()) {
                final ClientProfile receiverProfile = receiverProfileOptional.get();
                final ClientProfile senderProfile = senderProfileOptional.get();

                final Optional<Chat> chat = CHAT_MANAGER.chatBetweenUsersExist(receiverProfile, senderProfile);
                //Append to chat
                System.out.println("Does chat exist?: " + CHAT_MANAGER.getAllChats().stream().anyMatch(chat1 -> chat1.containsUsers(receiverProfile, senderProfile)));
                if (chat.isPresent()) {
                    System.out.println("Message appended!");
                    chat.get().appendMessage(senderProfile.getTag(), message);
                    //Send to receiver
                } else {
                    //Open new chat
                    final Chat newChat = CHAT_MANAGER.addChat(receiverProfile, senderProfile);
                   // CHAT_MANAGER.appendChat(newChat);
                    newChat.appendMessage(senderProfile.getTag(), message);
                    //Send message to receiver after adding it to the chat.
                }
                final EncoderPacket encoderPacket = new EncoderPacket()
                        .addArgument(message)
                        .addArgument(senderProfile.getTag());

                receiverMessageConsumer.accept(receiverProfile.getTag(), enc.format("CHT", "REC", encoderPacket));
                receiverMessageConsumer.accept(null, enc.format("CHT", "SUCCESS"));
            } else {
                if (receiverProfileOptional.isEmpty())
                    receiverMessageConsumer.accept(null, enc.format(ErrorType.RECEIVER_NOT_ONLINE));
                else if (senderProfileOptional.isEmpty())
                    receiverMessageConsumer.accept(null, enc.format(ErrorType.ACCOUNT_NOT_LOGGED_IN));
            }
        } else if (enc.getOperation().equals("GET")) {
            //Given arguments: 0 - partner
            final String chatPartnerTag = enc.getArgument(0);
            final Optional<ClientProfile> chatPartner = USER_MANAGER.getUser(chatPartnerTag);
            final Optional<ClientProfile> senderProfile = USER_MANAGER.getOnlineUser(ip, port);
            if (chatPartner.isPresent() && senderProfile.isPresent()) {
                final Optional<Chat> optionalChat = CHAT_MANAGER.chatBetweenUsersExist(chatPartner.get(), senderProfile.get());
                if (optionalChat.isPresent()) {
                    final Chat chat = optionalChat.get();
                    //Return messages
                    //Flood the user with new receives
                    for (ChatMessage message : chat.messages) {
                        final EncoderPacket encoderPacket = new EncoderPacket()
                                .addArgument(message.getMessage())
                                .addArgument(message.getSender());

                        receiverMessageConsumer.accept(null, enc.format("CHT", "REC", encoderPacket));
                    }
                } else {
                    //Return nothing, send an empty list
                    final EncoderPacket encoderPacket = new EncoderPacket();
                    encoderPacket.addArgument(new ArrayList<>());
                    receiverMessageConsumer.accept(null, enc.format("CHT", "GET", encoderPacket));
                }
            } else {
                if (chatPartner.isEmpty())
                    receiverMessageConsumer.accept(null, enc.format(ErrorType.RECEIVER_NOT_ONLINE));
                else receiverMessageConsumer.accept(null, enc.format(ErrorType.ACCOUNT_NOT_LOGGED_IN));
            }
        }
    }
}
