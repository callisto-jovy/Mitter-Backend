import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class UserHandler implements Constant {

    public static void handleChat(final EncoderUtil enc, final String ip, final int port, final Consumer<String> sendReturn) {
        //Check whether the sender is even online
        if (!USER_HANDLER.isUserOnline(ip, port)) {
            sendReturn.accept(enc.format(ErrorType.ACCOUNT_NOT_LOGGED_IN));
            return;
        }
        if (enc.getOperation().equals("GET ALL")) {
            final List<String> userTags = USER_HANDLER.getProfiles();
            final EncoderPacket encoderPacket = new EncoderPacket()
                    .addList(userTags);
            sendReturn.accept(enc.format("USR", "ALL", encoderPacket));
        } else if (enc.getOperation().equals("GET ONLINE")) {
            final List<String> userTags = USER_HANDLER.getActiveUsers();
            final EncoderPacket encoderPacket = new EncoderPacket()
                    .addList(userTags);
            sendReturn.accept(enc.format("USR", "ONLINE", encoderPacket));
        } else if (enc.getOperation().equals("GET")) {
            //Given argument: 0 - tag
            final String userTag = enc.getArgument(0);
            //Resolve user
            Optional<ClientProfile> optionalClientProfile = USER_HANDLER.getUser(userTag);
            if (optionalClientProfile.isPresent()) {
                final ClientProfile clientProfile = optionalClientProfile.get();
                final EncoderPacket encoderPacket = new EncoderPacket()
                        .addArgument(userTag)
                        .addArgument(clientProfile.getUsername())
                        .addArgument(clientProfile.getProfilePicture());
                sendReturn.accept(enc.format("USR", "GET", encoderPacket));
            } else {
                sendReturn.accept(enc.format(ErrorType.ACCOUNT_DOES_NOT_EXIST));
            }
        }
    }
}
