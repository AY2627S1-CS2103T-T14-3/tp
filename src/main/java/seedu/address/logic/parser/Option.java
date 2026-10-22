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

    private final Kind kind;
    private final List<String> aliases;

    private Option(Kind kind, String... aliases) {
        this.kind = requireNonNull(kind);
        if (aliases == null) {
            throw new NullPointerException("Option aliases must not be null");
        }
        if (aliases.length == 0) {
            throw new IllegalArgumentException("There must be at least one option aliases");
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
    public static Option required(String... aliases) {
        return new Option(Kind.REQUIRED, aliases);
    }

    /** Creates an option which may be omitted, but must have a value when present. */
    public static Option optional(String... aliases) {
        return new Option(Kind.OPTIONAL, aliases);
    }

    /** Creates an option which takes no value. */
    public static Option flag(String... aliases) {
        return new Option(Kind.FLAG, aliases);
    }

    public Kind getKind() {
        return kind;
    }

    public List<String> getAliases() {
        return aliases;
    }
}
