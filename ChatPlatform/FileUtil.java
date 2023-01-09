import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Base64;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * The server's file saving system.
 * The system itself consists out of two parts:
 * The user-file
 * The chat-files
 * <p>
 * - users.json
 * - chats
 * - chat0.txt
 * - chat1.txt
 * - ...
 */
public class FileUtil implements Constant {

    public static final File MAIN_DIR = new File("saved_state");
    public static final File USER_FILE = new File(MAIN_DIR, "users.json");
    public static final File CHAT_DIR = new File(MAIN_DIR, "chats");
    public static final File PICTURES_DIR = new File(MAIN_DIR, "pictures");

    public FileUtil() {
        this.checkFileStructure();
    }

    public void saveSeverState() {
        this.checkFileStructure();
        //Save all signed-up users
        this.saveUsers();
        this.saveChats();
    }

    private void checkFileStructure() {
        if (!MAIN_DIR.exists()) {
            MAIN_DIR.mkdir();
        }

        if (!CHAT_DIR.exists()) {
            CHAT_DIR.mkdirs();
        }

        if (!PICTURES_DIR.exists()) {
            PICTURES_DIR.mkdir();
        }

        if (!USER_FILE.exists()) {
            try {
                USER_FILE.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

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

        final JSONArray usersArray = new JSONArray();
        USER_MANAGER.getProfiles().forEach(clientProfile -> usersArray.put(encodeUser(clientProfile)));

        try (final BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(USER_FILE))) {
            bufferedWriter.write(usersArray.toString());
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

            final JSONArray messageArray = new JSONArray();

            final JSONObject containerObject = new JSONObject()
                    .put("metadata", new JSONArray()
                            .put(allChat.getUser1())
                            .put(allChat.getUser2()));

            containerObject.put("msgs", messageArray);

            try (final BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(chatFile))) {
                if (!chatFile.exists()) {
                    chatFile.createNewFile();
                }

                for (final ChatMessage message : allChat.messages) {
                    final JSONObject messageObject = new JSONObject()
                            .put("msg", message.getMessage())
                            .put("sender", message.getSender());

                    messageArray.put(messageObject);
                }

                bufferedWriter.write(containerObject.toString());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void loadUsers() {
        try (final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(USER_FILE)))) {
            final String line = bufferedReader.lines().collect(Collectors.joining());
            final JSONArray usersArray = new JSONArray(line);

            for (int i = 0; i < usersArray.length(); i++) {
                final JSONObject userObject = usersArray.getJSONObject(i);
                final ClientProfile clientProfile = decodeUser(userObject);
                USER_MANAGER.addNewUser(clientProfile);
                System.out.println("User registered: " + clientProfile.getTag());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void loadChats() {
        if (CHAT_DIR.listFiles() != null) {
            for (final File file : Objects.requireNonNull(CHAT_DIR.listFiles())) {
                try (final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(file)))) {
                    final String line = bufferedReader.lines().collect(Collectors.joining());
                    final JSONObject containerObject = new JSONObject(line);
                    final JSONArray metadata = containerObject.getJSONArray("metadata");
                    final JSONArray messages = containerObject.getJSONArray("msgs");

                    final Chat chat = new Chat(metadata.getString(0), metadata.getString(1));

                    for (int i = 0; i < messages.length(); i++) {
                        final JSONObject messageObject = messages.getJSONObject(i);
                        final String sender = messageObject.getString("sender");
                        final String message = messageObject.getString("msg");

                        chat.appendMessage(new ChatMessage(sender, message));
                    }
                    CHAT_MANAGER.appendChat(chat);
                    System.out.printf("Chat loaded with %d messages%n", chat.getLength());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    private ClientProfile decodeUser(final JSONObject in) {
        return new ClientProfile(
                in.getString("username"),
                in.getString("pass"),
                in.getString("tag"),
                in.getString("pic")
        );
    }


    private JSONObject encodeUser(final ClientProfile profile) {
        return new JSONObject()
                .put("tag", profile.getTag())
                .put("username", profile.getUsername())
                .put("pass", profile.getPassword())
                .put("pic", profile.getProfilePicture());
    }

    /**
     * Writes a given base64 string as a blob to the disk
     *
     * @param base64 the image in base64 encoding
     * @return an id associated with the image (a generated uuid)
     */

    public static String writeImage(final String base64, final String previousId) {
        //Delete old blob to save space
        if (!previousId.equals("null")) {
            final File oldBlob = new File(PICTURES_DIR, previousId);
            if (oldBlob.exists())
                System.out.println("Deleting old blob: " + oldBlob.delete());
        }

        final byte[] bytes = Base64.getDecoder().decode(base64.getBytes(StandardCharsets.UTF_8));
        final String assignedId = UUID.randomUUID().toString();
        final File outFile = new File(PICTURES_DIR, assignedId);

        try (final FileOutputStream fos = new FileOutputStream(outFile)) {
            fos.write(bytes);
            fos.flush();
        } catch (IOException e) {
            e.printStackTrace();
            return "null";
        }
        return assignedId;
    }

    public static String readImage(final String id) {
        if (id.equals("null"))
            return "null";

        final File inFile = new File(PICTURES_DIR, id);
        try {
            final byte[] bytes = Files.readAllBytes(inFile.toPath());

            return Base64.getEncoder().encodeToString(bytes);
        } catch (final IOException e) {
            e.printStackTrace();
            return "null";
        }
    }


}
