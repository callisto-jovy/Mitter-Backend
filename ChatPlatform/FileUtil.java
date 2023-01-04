import java.io.*;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * The server's file saving system.
 * The system itself consists out of two parts:
 * The user-file
 * The chat-files
 * <p>
 * - users.txt
 * - chats
 * - chat0.txt
 * - chat1.txt
 * - ...
 */
public class FileUtil implements Constant {

    public static final File MAIN_DIR = new File("saved_state");
    public static final File USER_FILE = new File(MAIN_DIR, "users.txt");

    public static final File CHAT_DIR = new File(MAIN_DIR, "chats");

    public FileUtil() {
        if (!MAIN_DIR.exists()) {
            MAIN_DIR.mkdir();
        }
        if (!USER_FILE.exists()) {
            try {
                USER_FILE.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        if (!CHAT_DIR.exists()) {
            CHAT_DIR.mkdirs();
        }
    }

    public void saveSeverState() {
        //Save all signed-up users
        this.saveUsers();
        this.saveChats();
    }

    public void loadServerState() {
        this.loadUsers();
        this.loadChats();
    }

    private void saveUsers() {
        /*A user's profile in the file is formatted as follows:
             "Tag" "Username" "Password" "Profile-Picture" \n
        Every new line is another user.
        */
        try (final BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(USER_FILE))) {

            for (final ClientProfile profile : USER_MANAGER.getProfiles()) {
                final String formatted = formatUser(profile);
                bufferedWriter.write(formatted);
                bufferedWriter.newLine();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void saveChats() {
        //Delete every old file.. Is there a better way to do this? Yes. But I am too lazy to do it.
        try {
            Files.walk(CHAT_DIR.toPath()).forEach(path -> path.toFile().delete());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        CHAT_DIR.mkdir();

        for (final Chat allChat : CHAT_MANAGER.getAllChats()) {
            final String fileName = UUID.randomUUID().toString();
            final File chatFile = new File(CHAT_DIR, fileName);

            try (final BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(chatFile))) {
                if (!chatFile.exists()) {
                    chatFile.createNewFile();
                }
                //"Metadata"
                bufferedWriter.write(allChat.getUser1());
                bufferedWriter.newLine();
                bufferedWriter.write(allChat.getUser2());
                bufferedWriter.newLine();
                bufferedWriter.newLine();

                for (final ChatMessage message : allChat.messages) {
                    if (!message.getMessage().isEmpty()) {
                        bufferedWriter.write(formatMessage(message));
                        bufferedWriter.newLine();
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void loadUsers() {
        try (final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(USER_FILE)))) {
            bufferedReader.lines().forEach(s -> {
                final ClientProfile clientProfile = decodeUser(s);
                USER_MANAGER.addNewUser(clientProfile);
                System.out.println("User registered: " + clientProfile.getTag());
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    private void loadChats() {
        if (CHAT_DIR.listFiles() != null) {
            for (final File file : Objects.requireNonNull(CHAT_DIR.listFiles())) {
                try (final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(file)))) {
                    final String[] lines = bufferedReader.lines().toArray(String[]::new);

                    final Chat chat = new Chat(lines[0], lines[1]);

                    for (int i = 3; i < lines.length; i++) {
                        final String line = lines[i];
                        chat.appendMessage(decodeMessage(line));
                    }
                    CHAT_MANAGER.appendChat(chat);
                    System.out.printf("Chat loaded with %d messages%n", chat.getLength());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    private ClientProfile decodeUser(final String in) {
        /*
        0 - tag
        1 - username
        2 - password
        3 - profile picture
         */
        final String[] split = in.split("(?<!\\\\)\"");
        final String tag = split[0].replace("\\\"", "\"");
        final String username = split[1].replace("\\\"", "\"");
        final String password = split[2].replace("\\\"", "\"");
        final String profile = split.length >= 4 ? split[3].replace("\\\"", "\"") : "";

        return new ClientProfile(username, password, tag, profile);
    }

    private String formatMessage(final ChatMessage message) {
        return message.getSender().replaceAll(":", "\\$1") + ":" + message.getMessage();
    }

    private ChatMessage decodeMessage(final String in) {
        final String[] strings = in.split("(?<!\\\\):");
        final String tag = strings[0].replace("\"", "");
        final String msg = Arrays.stream(strings, 1, strings.length).collect(Collectors.joining());
        return new ChatMessage(tag, msg);
    }

    private String formatUser(final ClientProfile clientProfile) {
        return sanitizeProfileString(clientProfile.getTag()) + "\""
                + sanitizeProfileString(clientProfile.getUsername()) + "\""
                + sanitizeProfileString(clientProfile.getPassword()) + "\""
                + sanitizeProfileString(clientProfile.getProfilePicture()) + "\"";
    }

    private String sanitizeProfileString(final String in) {
        return in.replaceAll("\"", "\\\\\"");
    }

}
