import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class UserManager {
    private List<ClientProfile> profiles;
    private List<ClientProfile> onlineProfiles;


    public UserManager() {
        this.profiles = new ArrayList<>();
        this.onlineProfiles = new ArrayList<>();
    }

    public List<String> getProfiles() {
        return profiles.stream().map(p -> p.getTag()).collect(Collectors.toUnmodifiableList());
    }

    public List<String> getActiveUsers() {
        return profiles.stream().map(p -> p.getTag()).collect(Collectors.toUnmodifiableList());
    }

    public boolean addOnlineUser(final ClientProfile clientProfile) {
        if (onlineProfiles.contains(clientProfile))
            return false;
        return onlineProfiles.add(clientProfile);
    }

    public boolean isUserOnline(final String tag, final String ip, int port) {
        return onlineProfiles.stream().anyMatch(p -> p.getTag().equals(tag) && p.equals(ip) && p.getPortOnline() == port);
    }

    public boolean isUserOnline(final String ip, int port) {
        return onlineProfiles.stream().anyMatch(p -> p.equals(ip) && p.getPortOnline() == port);
    }

    public boolean isUserOnline(final String tag) {
        return onlineProfiles.stream().anyMatch(p -> p.getTag().equals(tag));
    }


    public boolean removeOnlineUser(final ClientProfile clientProfile) {
        if (onlineProfiles.contains(clientProfile))
            return onlineProfiles.remove(clientProfile);
        return false;
    }

    public boolean removeOnlineUser(final String tag) {
        return onlineProfiles.removeIf(p -> p.getTag().equals(tag));
    }

    public boolean doesUserExist(final String tag) {
        return profiles.stream().anyMatch(p -> p.getTag().equals(tag));
    }

    public boolean addNewUser(final ClientProfile clientProfile) {
        return profiles.add(clientProfile);
    }

    public Optional<ClientProfile> getUser(final String tag) {
        return profiles.stream().filter(p -> p.getTag().equals(tag)).findFirst();
    }

    public Optional<ClientProfile> getUser(final String ip, int port) {
        return profiles.stream().filter(p -> p.getCurrentIp().equals(ip) && p.getPortOnline() == port).findFirst();
    }

}
