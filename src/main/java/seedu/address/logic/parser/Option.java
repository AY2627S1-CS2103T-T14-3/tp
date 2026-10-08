package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Describes a command-line option and whether it accepts a value.
 */
public final class Option {

    private static final Pattern ALIAS_PATTERN = Pattern.compile("-[a-z]+(?:-[a-z]+)*");

    /** The ways in which an option may be used. */
    public enum Kind {
        REQUIRED,
        OPTIONAL,
        FLAG
    }

    private final Prefix prefix;
    private final Kind kind;
    private final List<String> aliases;

    private Option(Prefix prefix, Kind kind, String... aliases) {
        this.prefix = requireNonNull(prefix);
        this.kind = requireNonNull(kind);
        if (aliases == null) {
            throw new NullPointerException("Option aliases must not be null");
        }

        Set<String> uniqueAliases = new LinkedHashSet<>();
        for (String alias : aliases) {
            if (alias == null || !ALIAS_PATTERN.matcher(alias).matches() || !uniqueAliases.add(alias)) {
                throw new IllegalArgumentException("Option aliases must be unique hyphen-prefixed lowercase words");
            }
        }
        if (uniqueAliases.isEmpty()) {
            throw new IllegalArgumentException("An option must have at least one alias");
        }
        this.aliases = List.copyOf(uniqueAliases);
    }

    /** Creates an option which must be present and have a value. */
    public static Option required(Prefix prefix, String... aliases) {
        return new Option(prefix, Kind.REQUIRED, aliases);
    }

    /** Creates an option which may be omitted, but must have a value when present. */
    public static Option optional(Prefix prefix, String... aliases) {
        return new Option(prefix, Kind.OPTIONAL, aliases);
    }

    /** Creates an option which takes no value. */
    public static Option flag(Prefix prefix, String... aliases) {
        return new Option(prefix, Kind.FLAG, aliases);
    }

    public Prefix getPrefix() {
        return prefix;
    }

    public Kind getKind() {
        return kind;
    }

    public List<String> getAliases() {
        return aliases;
    }
}
