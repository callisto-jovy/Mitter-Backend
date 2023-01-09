import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class UserManager {
    private final List<ClientProfile> profiles;
    private final List<ClientProfile> onlineProfiles;


    public UserManager() {
        this.profiles = new ArrayList<>();
        this.onlineProfiles = new ArrayList<>();
    }

    public List<String> getTags() {
        return profiles.stream().map(ClientProfile::getTag).collect(Collectors.toList());
    }

    public List<ClientProfile> getProfiles() {
        return profiles;
    }

    public List<String> getActiveUsers() {
        return profiles.stream().map(ClientProfile::getTag).collect(Collectors.toList());
    }

    public boolean addOnlineUser(final ClientProfile clientProfile) {
        if (onlineProfiles.contains(clientProfile))
            return false;
        return onlineProfiles.add(clientProfile);
    }

    public boolean isUserOnline(final String tag, final String ip, int port) {
        return onlineProfiles.stream().anyMatch(p -> p.getTag().equals(tag) && p.getCurrentIp().equals(ip) && p.getPortOnline() == port);
    }

    public boolean isUserOnline(final String ip, int port) {
        return onlineProfiles.stream().anyMatch(p -> p.getCurrentIp().equals(ip) && p.getPortOnline() == port);
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

    public Optional<ClientProfile> getOnlineUser(final String ip, int port) {
        return onlineProfiles.stream().filter(p -> p.getCurrentIp().equals(ip) && p.getPortOnline() == port).findFirst();
    }



}
