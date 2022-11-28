import java.util.ArrayList;
import java.util.Optional;
import java.util.function.BiConsumer;

public class ChatHandler implements Constant {

    public static void handleChat(final EncoderUtil enc, final String ip, final int port, final BiConsumer<String, String> receiverMessageConsumer) {
        //Check whether the sender is even online
        if (!USER_HANDLER.isUserOnline(ip, port)) {
            receiverMessageConsumer.accept(null, enc.format(ErrorType.ACCOUNT_NOT_LOGGED_IN));
            return;
        }
        if (enc.getOperation().equals("WRT")) {
            //Given arguments 0 - receiver, 1 - message
            final String receiver = enc.getArgument(0);
            final String message = enc.getArgument(1);

            //final boolean targetOnline = USER_HANDLER.isUserOnline(receiver);
            /*
            //Receiver is not online, discard
            if (!targetOnline) {
                receiverMessageConsumer.accept(null, enc.format(ErrorType.RECEIVER_NOT_ONLINE, new String[]{receiver}));
                return;
            }

             */

            final Optional<ClientProfile> receiverProfile = USER_HANDLER.getUser(receiver);
            final Optional<ClientProfile> senderProfile = USER_HANDLER.getUser(ip, port);
            if (receiverProfile.isPresent() && senderProfile.isPresent()) {
                final Optional<Chat> chat = CHAT_MANAGER.chatBetweenUsersExist(receiverProfile.get(), senderProfile.get());
                //Append to chat
                if (chat.isPresent()) {
                    chat.get().appendMessage(senderProfile.get().getTag(), message);
                    //Send to receiver
                } else {
                    //Open new chat
                    final Chat newChat = CHAT_MANAGER.addChat(receiverProfile.get(), senderProfile.get());
                    CHAT_MANAGER.appendToChat(newChat, senderProfile.get());
                    newChat.appendMessage(senderProfile.get().getTag(), message);
                    //Send message to receiver after adding it to the chat.
                }
                final EncoderPacket encoderPacket = new EncoderPacket()
                        .addArgument(message)
                        .addArgument(senderProfile.get().getTag());

                receiverMessageConsumer.accept(receiverProfile.get().getTag(), enc.format("CHT", "REC", encoderPacket));
            } else {
                if (!receiverProfile.isPresent())
                    receiverMessageConsumer.accept(null, enc.format(ErrorType.RECEIVER_NOT_ONLINE));
                else if (!senderProfile.isPresent())
                    receiverMessageConsumer.accept(null, enc.format(ErrorType.ACCOUNT_NOT_LOGGED_IN));
            }
        } else if (enc.getOperation().equals("GET")) {
            //Given arguments: 0 - partner
            final String chatPartnerTag = enc.getArgument(0);
            final Optional<ClientProfile> chatPartner = USER_HANDLER.getUser(chatPartnerTag);
            final Optional<ClientProfile> senderProfile = USER_HANDLER.getUser(ip, port);
            if (chatPartner.isPresent() && senderProfile.isPresent()) {
                final Optional<Chat> optionalChat = CHAT_MANAGER.chatBetweenUsersExist(chatPartner.get(), senderProfile.get());
                if (optionalChat.isPresent()) {
                    final Chat chat = optionalChat.get();
                    //Return messages
                    final EncoderPacket encoderPacket = new EncoderPacket();
                    encoderPacket.addArgument(chat.messages.stream().map(m -> m.toString()).toList());
                    receiverMessageConsumer.accept(null, enc.format("CHT", "GET", encoderPacket));
                } else {
                    //Return nothing, send an empty list
                    final EncoderPacket encoderPacket = new EncoderPacket();
                    encoderPacket.addArgument(new ArrayList<>());
                    receiverMessageConsumer.accept(null, enc.format("CHT", "GET", encoderPacket));
                }
            } else {
                if (!chatPartner.isPresent())
                    receiverMessageConsumer.accept(null, enc.format(ErrorType.RECEIVER_NOT_ONLINE));
                else if (!senderProfile.isPresent())
                    receiverMessageConsumer.accept(null, enc.format(ErrorType.ACCOUNT_NOT_LOGGED_IN));
            }
        }
    }
}
