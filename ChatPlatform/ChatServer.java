import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ChatServer extends Server implements Constant {
    private final String ENDE = "*bye*";


    public ChatServer() {
        super(2000); //h�rt auf Port 2000 - man sollte immer Ports �ber 1000 benutzen, um nicht mit Standard-Server Diensten zu kollidieren

        InetAddress ip;
        try {
            ip = InetAddress.getLocalHost();
            System.out.println("Your current IP address : " + ip);
            System.out.println("Your Port to listen is : " + 2000);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

    }

    public void processNewConnection(String pClientIP, int pClientPort) {
        this.send(pClientIP, pClientPort, "Willkommen, hier kannst du chatten! Log dich ein oder Erstelle einen Account, um loszulegen");  // sendet Nachricht an Client
    }

    public void processMessage(String pClientIP, int pClientPort, String pMessage) {
        if (pMessage.equals(ENDE)) {
            this.closeConnection(pClientIP, pClientPort);   // hier wird die Verbindung beendet. Methode aus der Oberklasse.
        } else {
            //this.send(pClientIP, pClientPort, pClientIP + " " + pClientPort + ": " + pMessage); // sendet Nachricht an Client
            //System.out.println(" " + pClientIP + ": " + pMessage); // gibt alles auf der Server-Konsole aus
            firstAttribute(pMessage, pClientIP, pClientPort);
        }

    }

    public void processClosingConnection(String pClientIP, int pClientPort) {
        this.send(pClientIP, pClientPort, pClientIP + " " + pClientPort + " auf Wiedersehen. ");
        System.out.println(pClientIP + " " + pClientPort + " auf Wiedersehen. ");
        this.send(pClientIP, pClientPort, ENDE);
    }

    void firstAttribute(String pString, String ip, int pClientPort) {
        final EncoderUtil enc = new EncoderUtil(pString);
        System.out.println(enc.getOperation());

        switch (enc.getID()) {
            case "ACC":
                accMessage(enc, ip, pClientPort);
                break;
            case "CHT":
                chtMessage(enc, ip, pClientPort);
                break;
            case "USR":
                usrMessage(enc, ip, pClientPort);
                break;
            default:
                System.out.println("*Server* corrupted Message: " + ip + ": " + pString);
                break;
        }

    }

    void accMessage(EncoderUtil enc, String ip, int port) {
        AccountHandler.handleAccountMessage(enc, ip, port, s -> send(ip, port, s));
    }

    void chtMessage(EncoderUtil enc, String ip, int pClientPort) {
        ChatHandler.handleChat(enc, ip, pClientPort, (t, m) -> {
            //Return to sender
            if (t == null) {
                send(ip, pClientPort, m);
            } else {
                final Optional<ClientProfile> receiver = USER_HANDLER.getUser(t);
                receiver.ifPresent(p -> send(p.getCurrentIp(), p.getPortOnline(), m));
            }
        });

    }

    void usrMessage(EncoderUtil enc, String ip, int pClientPort) {
        /*
        switch (enc.getOperation()) {
            case "GETALL":
                sendList(enc, allAccounts, "ALL", ip, pClientPort);
                break;
            case "GETONL":
                sendList(enc, accountsOnline, "ONL", ip, pClientPort);
                break;
            case "GETCHT":
                List<Chat> chats = new ArrayList<>();
                ClientProfile c = ClientProfile.existsUserIP(allAccounts, ip);
                if (!c.username.equals("")) {
                    for (int i = 0; i < allChats.size(); i++) {
                        if (allChats.get(i).hasUser(c))
                            chats.add(allChats.get(i));
                    }
                }
                sendList(enc, chats, "CHT", ip, pClientPort, c);
                break;
            default:
                System.out.println("*Server* corrupted Message: " + ip + ": " + enc.getOperation());
                break;
        }

         */
    }


}
