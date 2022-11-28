import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class EncoderPacket {
    private final List<String> arguments;

    public EncoderPacket() {
        this.arguments = new ArrayList<>();
    }

    public EncoderPacket addArgument(final String string) {
        this.arguments.add(sanitizeString(string));
        return this;
    }

    public EncoderPacket addArgument(final String... strings) {
        for (final String string : strings) {
            this.arguments.add(sanitizeString(string));
        }
        return this;
    }

    public EncoderPacket addArgument(final List<String> strings) {
        for (final String string : strings) {
            this.arguments.add(sanitizeString(string));
        }
        return this;
    }

    public EncoderPacket addList(final List<String> strings) {
        //Format to list format
        final StringBuilder builder = new StringBuilder("[");
        for (int i = 0; i < strings.size(); i++) {
            final String str = strings.get(i)
                    .replace(",", "\\,")
                    .replaceAll("(\\[|\\])", "\\\\$1");
            builder.append(sanitizeString(str));
            if (i < strings.size() - 1) {
                builder.append(",");
            }
        }
        builder.append("]");
        arguments.add(builder.toString());
        return this;
    }

    private String sanitizeString(final String string) {
        return string.replace(EncoderUtil.DELIMITER, EncoderUtil.ESCAPED_DELIMITER);
    }

    @Override
    public String toString() {
        return arguments.stream().collect(Collectors.joining(EncoderUtil.DELIMITER));
    }
}
