import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

public class EncoderPacket {
    private final JSONArray arguments;

    public EncoderPacket() {
        this.arguments = new JSONArray();
    }

    public EncoderPacket addArgument(final String string) {
        this.arguments.put(string);
        return this;
    }

    public EncoderPacket addArgument(final String... strings) {
        for (String string : strings) {
            arguments.put(string);
        }
        return this;
    }

    public EncoderPacket addArgument(final JSONObject jsonObject) {
        arguments.put(jsonObject);
        return this;
    }

    public EncoderPacket addList(final List<String> strings) {
        arguments.put(strings);
        return this;
    }

    public JSONArray toJSONArray() {
        return arguments;
    }
}
