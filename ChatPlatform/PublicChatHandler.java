import java.util.Optional;
import java.util.function.BiConsumer;

public class PublicChatHandler implements Constant {

    public static void handleChat(final EncoderUtil enc, final String ip, final int port, final BiConsumer<Boolean, String> receiverMessageConsumer) {
        //Check whether the sender is even online
        if (!USER_MANAGER.isUserOnline(ip, port)) {
            receiverMessageConsumer.accept(false, enc.format(ErrorType.ACCOUNT_NOT_LOGGED_IN));
            return;
        }

        if(enc.getOperation().equals("WRT")) {
            final Optional<ClientProfile> sender = USER_MANAGER.getOnlineUser(ip, port);
            final String message = enc.getArgument(0);

            if(sender.isPresent()) {
                final EncoderPacket encoderPacket = new EncoderPacket()
                        .addArgument(message)
                        .addArgument(sender.get().getTag());

                receiverMessageConsumer.accept(true, enc.format("PUB", "REC", encoderPacket));
            } else {
                receiverMessageConsumer.accept(false, enc.format(ErrorType.ACCOUNT_NOT_LOGGED_IN));
            }
        }
        //TODO: Delete
    }

}
