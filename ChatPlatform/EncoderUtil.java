import org.json.JSONArray;
import org.json.JSONObject;

public class EncoderUtil {

    /*
    public static final String DELIMITER = ";";
    public static final String ESCAPED_DELIMITER = "\\;";

     */

    public static final String KEY_ID = "id";
    public static final String KEY_OPERATION = "op";
    public static final String KEY_STAMP = "stamp";
    public static final String KEY_ARGUMENTS = "args";


    private final String input;

    private final JSONObject internalObject;
    private final JSONArray arguments;

    public EncoderUtil(final String input) {
        this.input = input;
        if (!this.validateInput()) {
            throw new IllegalStateException("Invalid input string supplied");
        }
        this.internalObject = new JSONObject(input);
        this.arguments = internalObject.getJSONArray(KEY_ARGUMENTS);
    }

    private boolean validateInput() {
        return input != null && !input.isEmpty();
        //TODO: JSON valid?
    }

    /*
    Old approach, did not work out...
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


     */

    public String getID() {
        return internalObject.getString(KEY_ID);
    }

    public String getOperation() {
        return internalObject.getString(KEY_OPERATION);
    }

    public String getStamp() {
        return internalObject.getString(KEY_STAMP);
    }

    public <T> T getArgument(final int index) {
        return (T) arguments.opt(index) ;
    }

    private String formatBasic(final String packetID, final String operation, final EncoderPacket packet) {
        return new JSONObject()
                .put(KEY_ID, packetID)
                .put(KEY_OPERATION, operation)
                .put(KEY_STAMP, getStamp())
                .put(KEY_ARGUMENTS, packet == null ? new JSONArray() : packet.toJSONArray())
                .toString();

    }

    public String format(final String packetID, final String operation, final EncoderPacket packet) {
        return formatBasic(packetID, operation, packet);
    }

    public String format(final String packetID, final String operation) {
        return formatBasic(packetID, operation, null);
    }

    public String format(ErrorType errorType) {
        return formatBasic("ERR", String.valueOf(errorType.getCode()), null);
    }
}
