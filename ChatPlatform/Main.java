public class Main {

    public static void main(String[] args) {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            new FileUtil().saveSeverState();
        }));
        new FileUtil().loadServerState();
        final ChatServer chatServer = new ChatServer();
    }
}
