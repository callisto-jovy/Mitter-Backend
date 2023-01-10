import java.util.Optional;
import java.util.function.Consumer;

public class AccountHandler implements Constant {

    public static void handleAccountMessage(final EncoderUtil enc, String ip, int port, Consumer<String> sendReturn) {
        //CREATE new account
        if (enc.getOperation().equals("CRT")) {
            //Create Login (Arguments given: 0 - Username, 1 - tag, 2 - password)
            final String tag = enc.getArgument(1);
            final String username = enc.getArgument(0);
            final String password = enc.getArgument(2);

            if (USER_MANAGER.doesUserExist(tag)) {
                sendReturn.accept(enc.format(ErrorType.ACCOUNT_TAG_ALREADY_TAKEN));
                return;
            }

            final ClientProfile clientProfile = new ClientProfile(username, password, tag, ip, port);
            USER_MANAGER.addNewUser(clientProfile);
            sendReturn.accept(enc.format("ACC", "CREATED"));
        } else if (enc.getOperation().equals("LIN")) {
            //Given arguments: 0 - tag, 1 - password
            final String tag = enc.getArgument(0);
            final Optional<ClientProfile> optionalClientProfile = USER_MANAGER.getUser(tag);
            if (optionalClientProfile.isPresent()) {
                final String password = enc.getArgument(1);
                final ClientProfile clientProfile = optionalClientProfile.get();

                if (clientProfile.tryLogin(tag, password, ip, port)) {
                    USER_MANAGER.addOnlineUser(clientProfile);
                    final EncoderPacket encoderPacket = new EncoderPacket()
                            .addArgument(clientProfile.toJSON())
                            .addList(CHAT_MANAGER.getAllChatPartners(clientProfile));

                    sendReturn.accept(enc.format("ACC", "COMPLETE", encoderPacket));
                } else {
                    sendReturn.accept(enc.format(ErrorType.ACCOUNT_LOGIN_FAILED));
                }
            } else {
                sendReturn.accept(enc.format(ErrorType.ACCOUNT_DOES_NOT_EXIST));
            }
        } else if (enc.getOperation().equals("LOT")) {
            //Arguments given: 0 - tag
            final String tag = enc.getArgument(0);

            if (USER_MANAGER.isUserOnline(tag, ip, port)) {
                if (USER_MANAGER.removeOnlineUser(tag)) {
                    USER_MANAGER.getUser(tag).ifPresent(ClientProfile::logout);
                    sendReturn.accept(enc.format("ACC", "LOGGED OUT"));
                } else {
                    sendReturn.accept(enc.format(ErrorType.ACCOUNT_NOT_LOGGED_OUT));
                }
            } else {
                sendReturn.accept(enc.format(ErrorType.ACCOUNT_NOT_LOGGED_IN));
            }
        } else if (enc.getOperation().equals("PROFILE")) {
            //Given argument: 0 - Profile picture (base64)
            final Optional<ClientProfile> optionalClientProfile = USER_MANAGER.getOnlineUser(ip, port);
            if (optionalClientProfile.isPresent()) {
                final String base64ProfilePicture = enc.getArgument(0);
                optionalClientProfile.get().setProfilePicture(base64ProfilePicture);
                sendReturn.accept(enc.format("ACC", "PROFILE SET"));
            } else {
                sendReturn.accept(enc.format(ErrorType.ACCOUNT_DOES_NOT_EXIST));
            }
        }
    }
}
