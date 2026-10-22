package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_DUPLICATE_FIELDS;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import seedu.address.logic.parser.Option.Kind;
import seedu.address.logic.parser.exceptions.ParseException;


/**
 * Tokenizes arguments string of the form: {@code preamble <option> value <option> value ...}<br>
 *     e.g. {@code some preamble text -t 11.00 -t 12.00 -k -m July}  where prefixes are {@code -t -k -m}.<br>
 * 1. An argument's value can be an empty string e.g. the value of {@code -k} in the above example.<br>
 * 2. Leading and trailing whitespaces of an argument value will be discarded.<br>
 * 3. An argument may be repeated and all its values will be accumulated e.g. the value of {@code -t}
 *    in the above example.<br>
 */
public class ArgumentTokenizer {

    public static final String MESSAGE_INVALID_COMMAND_OPTIONS = "Unable to parse command options.";

    /** A whitespace-delimited token which looks like an option under the command grammar. */
    private static final Pattern OPTION_TOKEN = Pattern.compile("(?<!\\S)(-[a-z]+(?:-[a-z]+)*)(?=\\s|$)");

    /**
     * Strictly tokenizes hyphen-prefixed command options according to {@code options}.
     *
     * <p>Aliases are canonicalized to the {@link Option} held by their option. Unknown options,
     * duplicates, absent values, positional text, and values containing option-like tokens are
     * rejected with the same parser-level error.</p>
     *
     * @param argsString command arguments containing only configured options
     * @param options required, optional, and flag-only option specifications
     * @return the parsed options, keyed by their canonical options
     * @throws ParseException if the input or option configuration is malformed
     */
    public static ArgumentMultimap tokenizeOptions(String argsString, boolean hasPreamble, Option... options)
            throws ParseException {
        if (argsString == null || options == null) {
            throw new NullPointerException("Arguments and options must not be null");
        }

        Map<String, Option> optionsByAlias = buildAliasMap(options);
        List<OptionOccurrence> occurrences = findOptionOccurrences(argsString, optionsByAlias);

        if (hasPreamble) {
            addPositionalPreamble(argsString, occurrences);
        } else {
            validateNoPositionalPreamble(argsString, occurrences);
        }

        ArgumentMultimap result = new ArgumentMultimap();
        Set<Option> seenOptions = new HashSet<>();
        Set<Option> dupeOptions = new HashSet<>();
        for (int i = 0; i < occurrences.size(); i++) {
            OptionOccurrence occurrence = occurrences.get(i);
            Option option = occurrence.option();
            if (!seenOptions.add(option)) {
                dupeOptions.add(option);
            }

            int valueEnd = i + 1 < occurrences.size()
                    ? occurrences.get(i + 1).startPosition()
                    : argsString.length();
            String value = argsString.substring(occurrence.endPosition(), valueEnd).trim();
            if ((option.getKind() == Kind.FLAG && !value.isEmpty())
                    || (option != Option.OPTION_EMPTY && option.getKind() != Kind.FLAG && value.isEmpty())) {
                throw malformedOptions();
            }
            result.put(option, value);
        }

        if (!dupeOptions.isEmpty()) {
            throw multipleOptions(dupeOptions);
        }

        boolean missingRequiredOption = Arrays.stream(options)
                .anyMatch(option -> option.getKind() == Kind.REQUIRED && !result.contains(option));
        if (missingRequiredOption) {
            throw malformedOptions();
        }
        return result;
    }

    private static Map<String, Option> buildAliasMap(Option... options) {
        Map<String, Option> optionsByAlias = new HashMap<>();
        Set<Option> configuredOptions = new HashSet<>();
        for (Option option : options) {
            if (option == null || !configuredOptions.add(option)) {
                throw new IllegalArgumentException("Options must be non-null and have unique canonical prefixes");
            }
            for (String alias : option.getAliases()) {
                if (optionsByAlias.putIfAbsent(alias, option) != null) {
                    throw new IllegalArgumentException("Aliases must be unique across options: " + alias);
                }
            }
        }
        return optionsByAlias;
    }

    private static List<OptionOccurrence> findOptionOccurrences(
            String argsString, Map<String, Option> optionsByAlias) throws ParseException {
        List<OptionOccurrence> occurrences = new ArrayList<>();
        Matcher matcher = OPTION_TOKEN.matcher(argsString);
        while (matcher.find()) {
            Option option = optionsByAlias.get(matcher.group(1));
            if (option == null) {
                throw malformedOptions();
            }
            occurrences.add(new OptionOccurrence(option, matcher.start(), matcher.end()));
        }
        return occurrences;
    }

    private static void validateNoPositionalPreamble(String argsString, List<OptionOccurrence> occurrences)
            throws ParseException {
        int firstOptionStart = occurrences.isEmpty() ? argsString.length() : occurrences.getFirst().startPosition();
        if (!argsString.substring(0, firstOptionStart).trim().isEmpty()) {
            throw malformedOptions();
        }
    }

    private static List<OptionOccurrence> addPositionalPreamble(String argsString, List<OptionOccurrence> occurrences)
            throws ParseException {
        int firstOptionStart = occurrences.isEmpty() ? argsString.length() : occurrences.getFirst().startPosition();
        occurrences.addFirst(new OptionOccurrence(Option.OPTION_EMPTY, 0, 0));
        return occurrences;
    }

    private static ParseException malformedOptions() {
        return new ParseException(MESSAGE_INVALID_COMMAND_OPTIONS);
    }
    private static ParseException multipleOptions(Set<Option> errorOptions) {
        assert !errorOptions.isEmpty();

        Set<String> duplicateFields =
                errorOptions.stream().map(Option::toString).collect(Collectors.toSet());

        return new ParseException(MESSAGE_DUPLICATE_FIELDS + String.join(" ", duplicateFields));
    }

    private record OptionOccurrence(Option option, int startPosition, int endPosition) { }
}
