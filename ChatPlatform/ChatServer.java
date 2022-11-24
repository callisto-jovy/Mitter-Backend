import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;

public class ChatServer extends Server
{
    private final String ENDE = "*bye*";
    List<ClientProfile> allAccounts = new ArrayList<>();
    List<ClientProfile> accountsOnline = new ArrayList<>();
    List<ChatContent> allChats = new ArrayList<>();

    public ChatServer()
    {   
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

    public void processNewConnection(String pClientIP, int pClientPort)
    {
        this.send(pClientIP, pClientPort, "Willkommen, hier kannst du chatten! Log dich ein oder Erstelle einen Account, um loszulegen");  // sendet Nachricht an Client
    }

    public void processMessage(String pClientIP, int pClientPort, String pMessage){
        if (pMessage.equals(ENDE)){
            this.closeConnection(pClientIP, pClientPort);   // hier wird die Verbindung beendet. Methode aus der Oberklasse.
        }
        else{
            //this.send(pClientIP, pClientPort, pClientIP + " " + pClientPort + ": " + pMessage); // sendet Nachricht an Client
            //System.out.println(" " + pClientIP + ": " + pMessage); // gibt alles auf der Server-Konsole aus
            firstAttribute(pMessage, pClientIP, pClientPort);
        }

    }

    public void processClosingConnection (String pClientIP, int pClientPort){
        this.send(pClientIP, pClientPort, pClientIP + " " + pClientPort + " auf Wiedersehen. ");
        System.out.println(pClientIP + " " + pClientPort + " auf Wiedersehen. ");
        this.send(pClientIP, pClientPort, ENDE);
    }

    void firstAttribute(String pString, String ip, int pClientPort){
        final EncoderUtil enc = new EncoderUtil(pString);
        System.out.println(enc.getOperation());

        switch(enc.getID()){
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
                System.out.println("*Server* corrupted Message: " + ip + ": "  + pString);
                break;
        }

    }

    void accMessage(EncoderUtil enc, String ip, int pClientPort){        
        switch(enc.getOperation()){
            case "CRT":
                ClientProfile cp = new ClientProfile(enc.getArgument(1), ip, pClientPort);
                cp.setPassword(enc.getArgument(2));
                System.out.println("Adding account!");
                allAccounts.add(cp);
                this.send(ip, pClientPort, enc.formatPacket("ACC", "CREATED"));

                //Create Login (Arguments given: 0 - Username, 1 - tag, 2 - password)
                //TODO:

                /*
                if(ClientProfile.checkUserPassRule(pElements.get(1))){
                ClientProfile cp = ClientProfile.existsUser(allAccounts, pElements.get(1));
                if(cp.username.equals(""))
                {
                allAccounts.add(new ClientProfile(pElements.get(1), ip, pClientPort));
                this.send(ip, pClientPort, "ACC CR PW enter password");
                }
                else{
                if(!cp.profileComplete && pElements.size() == 3){
                if(ClientProfile.checkUserPassRule(pElements.get(2)))
                {
                cp.setPassword(pElements.get(2));
                accountsOnline.add(cp);
                this.send(ip, pClientPort, "ACC CR DNE account created");
                }
                else
                this.send(ip, pClientPort, "ERR 103 password not ok");
                }
                else
                this.send(ip, pClientPort, "ERR 101 username taken");
                }
                } else
                this.send(ip, pClientPort, "ERR 102 username not ok");  
                 */
                break;
            case "LIN":
                //Given arguments: 0 - tag, 1 - password

                for (ClientProfile allAccount : allAccounts) {
                    System.out.println(allAccount.username);
                    System.out.println(allAccount.password);
                }
                System.out.println(enc.getArgument(1));
                ClientProfile ep = ClientProfile.existsUser(allAccounts, enc.getArgument(0));
                if(ep.username.equals(""))
                    this.send(ip, pClientPort , enc.formatPacket("ACC", "ERR 121"));
                else
                {
                    if(ep.tryLogin(enc.getArgument(0), enc.getArgument(1), ip, pClientPort)){
                        accountsOnline.add(ep);
                        this.send(ip, pClientPort , enc.formatPacket("ACC", "COMPLETE"));
                    }
                    else
                        this.send(ip, pClientPort , enc.formatPacket("ERR", "122"));
                }
                break;
            case "LOT":
                ClientProfile d = ClientProfile.existsUserIP(accountsOnline, ip);
                if(!d.ipAdress_online.equals("")){
                    d.logOUT();
                    accountsOnline.remove(d);
                }
                this.send(ip, pClientPort, enc.formatPacket("ACC", "LOGGED OUT"));
                break;
            default:
                System.out.println("*Server* corrupted Message: " + ip + ": "  + enc.getOperation());
                break;
        }
    }

    void chtMessage(EncoderUtil enc, String ip, int pClientPort){
        switch(enc.getOperation()){
            case "WRT":
                System.out.println(enc.getTokens());
                //Given arguments 0 - receiver, 1 - message
                final String receiver = enc.getArgument(0);
                final String message = enc.getArgument(1);

                ClientProfile target = ClientProfile.existsUser(allAccounts, receiver);
                if(target.username.equals(""))
                    this.send(ip, pClientPort , enc.formatPacket("ERR", "151", new String[] {receiver}));
                else
                {
                    ClientProfile sender = ClientProfile.existsUserIP(allAccounts, ip);
                    if(sender.username.equals(""))
                        this.send(ip, pClientPort , enc.formatPacket("ERR", "152"));

                    else{
                        //TODO: Formatting
                        boolean chatExists = false;
                        for(int i = 0; i < allChats.size(); i++){
                            if(allChats.get(i).hasUsers(target, sender)){
                                allChats.get(i).tryAddMessage(sender.username, message);
                                this.send(ip, pClientPort , "CHT RCV " + target.username + " " + message);
                                chatExists = true;
                                break;
                            }
                        }
                        if(!chatExists){
                            ChatContent cc = new ChatContent(sender, target);
                            cc.tryAddMessage(sender.username, message);
                            this.send(ip, pClientPort , "CHT RCV " + target.username + " " + message);
                            allChats.add(cc);
                        }
                        if(!target.ipAdress_online.equals(""))
                            this.send(target.ipAdress_online, target.port_online, "CHT NEW " + sender.username + " " + message);
                    }
                }
                break;
            case "GET":
                //Given arguments: 0 - partner
                String chatPartnerName = enc.getArgument(2);
                ClientProfile chatPartner = ClientProfile.existsUser(allAccounts, chatPartnerName);
                if(chatPartner.username.equals(""))
                    this.send(ip, pClientPort , enc.formatPacket("ERR", "141"));
                else
                {
                    ClientProfile sender = ClientProfile.existsUserIP(allAccounts, ip);
                    if(sender.username.equals(""))
                    this.send(ip, pClientPort , enc.formatPacket("ERR", "152"));
                    else{
                        boolean chatExists = false;
                        for(int i = 0; i < allChats.size(); i++){
                            if(allChats.get(i).hasUsers(chatPartner, sender)){
                                chatExists = true;
                                //OK
                                sendList(enc, allChats.get(i), ip, pClientPort, chatPartner);
                                break;
                            }
                        }
                        //TODO: Figure out, what this does and refactor it
                        if(!chatExists){
                            this.send(ip, pClientPort, "CHT START LIST " + chatPartner.username);
                        }
                        if(!chatPartner.ipAdress_online.equals(""))
                            this.send(ip, pClientPort, "CHT END LIST ONLINE " + chatPartner.username);
                        else
                            this.send(ip, pClientPort, "CHT END LIST OFFLINE " + chatPartner.username);
                    }
                }
                break;
                
            case "RCV":
                break;
            default:
                System.out.println("*Server* corrupted Message: " + ip + ": " + enc.getOperation());
                break;
        }
    }

    void usrMessage(EncoderUtil enc, String ip, int pClientPort){
        switch(enc.getOperation()){
            case "GETALL":
                sendList(enc, allAccounts, "ALL", ip, pClientPort);
                break;
            case "GETONL":
                sendList(enc, accountsOnline, "ONL", ip, pClientPort);
                break;
            case "GETCHT":
                List<ChatContent> chats = new ArrayList<>();
                ClientProfile c = ClientProfile.existsUserIP(allAccounts, ip);
                if(!c.username.equals("")){
                    for(int i = 0; i < allChats.size(); i++){
                        if(allChats.get(i).hasUser(c))
                            chats.add(allChats.get(i));
                    }
                }
                sendList(enc, chats, "CHT", ip, pClientPort, c);
                break;
            default:
                System.out.println("*Server* corrupted Message: " + ip + ": " + enc.getOperation());
                break;
        }
    }
    
    //TODO: Rework this

    void sendList(EncoderUtil enc, List<ClientProfile> users, String listName, String ip, int pClientPort){
        final List<String> args = users.stream().map(u -> u.username).toList();
        this.send(ip, pClientPort, enc.formatPacket("USR", "GET", args));
    }

    void sendList(EncoderUtil enc, List<ChatContent> chats, String listName, String ip, int pClientPort, ClientProfile user){
        this.send(ip, pClientPort, "USR START GET" + listName);
        for(int i = 0; i < chats.size(); i++){
            this.send(ip, pClientPort, "USR GET" + listName + " " + chats.get(i).getOtherUser(user).username);
        }
        this.send(ip, pClientPort, "USR END GET" + listName);
    }

    void sendList(EncoderUtil enc, ChatContent chat, String ip, int pClientPort, ClientProfile chatPartner){
        this.send(ip, pClientPort, "CHT START LIST " + chatPartner.username);
        for(int i = 0; i < chat.getLength(); i++){
            this.send(ip, pClientPort, "CHT LIST " + chatPartner.username + " " + chat.messages.get(i));
        }
    }


}
