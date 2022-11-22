import java.util.*;

public class ChatContent
{
    ClientProfile user1;
    ClientProfile user2;
    public List<String> messages = new ArrayList<>();
    
    public ChatContent(ClientProfile pUser1, ClientProfile pUser2){
        user1 = pUser1;
        user2 = pUser2;
    }
    
    public boolean hasUsers(String tryUser1, String tryUser2){
        return (tryUser1.equals(user1.username) && tryUser2.equals(user2.username)) || (tryUser2.equals(user1.username) && tryUser1.equals(user2.username));
    }
    
    public boolean hasUsers(ClientProfile tryUser1, ClientProfile tryUser2){
        return (tryUser1.username.equals(user1.username) && tryUser2.username.equals(user2.username)) || (tryUser2.username.equals(user1.username) && tryUser1.username.equals(user2.username));
    }
    
    public boolean hasUser(ClientProfile tryUser){
        return tryUser.username.equals(user1.username) || tryUser.username.equals(user2.username);
    }
    
    public ClientProfile getOtherUser(ClientProfile tryUser){
        if(tryUser.username.equals(user1.username))
            return user2;
        else if(tryUser.username.equals(user2.username))
            return user1;
        else
            {
                System.out.println("error 33");
                return new ClientProfile("", "", 0);
            }
    }
    
    public boolean tryAddMessage(String writingUser, String message){
        if(writingUser != user1.username && writingUser != user2.username)
            return false;
        messages.add(writingUser + " " + message);
        return true;
    }
    
    public int getLength(){
        return messages.size();
    }
    
    public String getWriter(int index){
        String message = messages.get(index);
        String writer = "";
        for(int i = 0; i < message.length(); i++){
            if (message.charAt(i) == ' ')
                break;
            else
                writer += message.charAt(i);
        }
        return writer;
    }
    
    public String getMessageContent(int index){
        String message = messages.get(index);
        boolean isContent = false;
        String content = "";
        for(int i = 0; i < message.length(); i++){
            if(isContent)
                content += message.charAt(i);
            else
                {
                    if (message.charAt(i) == ' ')
                        isContent = true;
                }
        }
        return content;
    }
}
