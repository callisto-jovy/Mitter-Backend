import java.util.*;
public class ClientProfile
{
    public String username;
    public String password;
    public boolean profileComplete = false;
    public String ipAdress_online;
    public int port_online;
    
    public ClientProfile(String pUsername, String pPassword, String pIP, int pPort){
        username = pUsername;
        password = pPassword;
        ipAdress_online = pIP;
        port_online = pPort;
        profileComplete = true;
    }
    
    public ClientProfile(String pUsername, String pIP, int pPort){
        username = pUsername;
        ipAdress_online = pIP;
        port_online = pPort;
    }
    
    public void setPassword(String pPassword){
        password = pPassword;
        profileComplete = true;
    }
    
    public boolean tryLogin(String pUsername, String pPassword, String pIP, int pPort){
        if(username.equals(pUsername) && password.equals(pPassword))
        {
            ipAdress_online = pIP;
            port_online = pPort;
            return true;
        }
        return false;
    }
    
    public static ClientProfile existsUser(List<ClientProfile> users, String pUsername){
        for(int i = 0; i < users.size(); i++){
            if(users.get(i).username.equals(pUsername)) 
                return users.get(i);
        }
        return new ClientProfile("", "", 0);
    }
    
    public static ClientProfile existsUserIP(List<ClientProfile> users, String pIP){
        for(int i = 0; i < users.size(); i++){
            if(users.get(i).ipAdress_online.equals(pIP)) 
                return users.get(i);
        }
        return new ClientProfile("", "", 0);
    }
    
    public static boolean checkUserPassRule(String usernameOrPassword){
        return !usernameOrPassword.contains(" ") && usernameOrPassword.length() <= 10 && usernameOrPassword != "";
    }
    
    public void logOUT(){
        ipAdress_online = "";
        port_online = 0;
    }
}
