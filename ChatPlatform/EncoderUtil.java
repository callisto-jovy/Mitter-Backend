import java.util.ArrayList;
import java.util.List;

public class EncoderUtil {

    public static final String DELIMITER = ";";
    public static final String ESCAPED_DELIMITER = "\\;";
    private final String input;
    private final List<String> tokens;

    public EncoderUtil(final String input) {
        this.input = input;
        if (!this.validateInput()) {
            throw new IllegalStateException("Invalid input string supplied");
        }
        this.tokens = tokenize();
    }

    private boolean validateInput() {
        if (input == null || input.isEmpty()) {
            return false;
        } else if (input.startsWith(";")) {
            return false;
        } else return input.split(";").length >= 3;
        //TODO: More checks
    }

    private List<String> tokenize() {
        final List<String> tokens = new ArrayList<>();
        int lastSemicolon = 0;
        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            //Escaped semicolons and brackets
            if (c == '\\') {
                i++;
                continue;
            }
            if (c == ';') {
                final String token = input.substring(lastSemicolon, i);
                tokens.add(token);
                lastSemicolon = i + 1;
            } else if (i + 1 == input.length()) {
                tokens.add(input.substring(lastSemicolon));
            }
        }
        return tokens;
    }

    public String getID() {
        return tokens.get(0);
    }

    public String getOperation() {
        return tokens.get(1);
    }

    public String getStamp() {
        return tokens.get(2);
    }

    public String getArgument(final int index) {
        if ((index + 3) > tokens.size() - 1) {
            return null;
        }
        return tokens.get(index + 3);
    }


    public List<String> getTokens() {
        return this.tokens;
    }

    public String format(final String packetID, final String operation, final EncoderPacket packet) {
        return packetID + ";" + operation + ";" + getStamp() + ";" + packet.toString();
    }

    public String format(final String packetID, final String operation) {
        return packetID + ";" + operation + ";" + getStamp() + ";";
    }

    public String format(ErrorType errorType) {
        return "ERR;" + errorType.getCode() + ";" + getStamp() + ";";
    }
}
