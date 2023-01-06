import org.json.JSONObject;

public class ClientProfile implements Constant {
    private final String username, password, tag;
    private String profilePicture = "null";
    private String currentIp;
    public int portOnline;


    public ClientProfile(String username, String password, String tag, String currentIp, int portOnline) {
        this.username = username;
        this.password = password;
        this.tag = tag;
        this.currentIp = currentIp;
        this.portOnline = portOnline;
    }

    public ClientProfile(String username, String password, String tag, String profilePicture) {
        this.username = username;
        this.password = password;
        this.tag = tag;
        this.profilePicture = profilePicture;
    }

    public boolean tryLogin(String tag, String password, String pIP, int pPort) {
        if (this.tag.equals(tag) && this.password.equals(password)) {
            this.currentIp = pIP;
            this.portOnline = pPort;
            return true;
        }
        return false;
    }


    public JSONObject toJSON() {
        return new JSONObject()
                .put("tag", getTag())
                .put("username", getUsername())
                .put("pic", getProfilePicture());
    }

    public static boolean checkUserPassRule(String usernameOrPassword) {
        return !usernameOrPassword.contains(" ") && usernameOrPassword.length() <= 10 && usernameOrPassword != "";
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getTag() {
        return tag;
    }

    public String getCurrentIp() {
        return currentIp;
    }

    public int getPortOnline() {
        return portOnline;
    }

    public void logout() {
        this.currentIp = "";
        this.portOnline = -1;
    }


}
