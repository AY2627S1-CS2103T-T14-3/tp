package seedu.address.logic.parser;

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

import seedu.address.logic.parser.Option.Kind;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Tokenizes arguments string of the form: {@code preamble <prefix>value <prefix>value ...}<br>
 *     e.g. {@code some preamble text t/ 11.00 t/12.00 k/ m/ July}  where prefixes are {@code t/ k/ m/}.<br>
 * 1. An argument's value can be an empty string e.g. the value of {@code k/} in the above example.<br>
 * 2. Leading and trailing whitespaces of an argument value will be discarded.<br>
 * 3. An argument may be repeated and all its values will be accumulated e.g. the value of {@code t/}
 *    in the above example.<br>
 */
public class ArgumentTokenizer {

    public static final String MESSAGE_INVALID_COMMAND_OPTIONS = "Unable to parse command options.";

    /** A whitespace-delimited token which looks like an option under the command grammar. */
    private static final Pattern OPTION_TOKEN = Pattern.compile("(?<!\\S)(-[a-z]+(?:-[a-z]+)*)(?=\\s|$)");

    /**
     * Tokenizes an arguments string and returns an {@code ArgumentMultimap} object that maps prefixes to their
     * respective argument values. Only the given prefixes will be recognized in the arguments string.
     *
     * @param argsString Arguments string of the form: {@code preamble <prefix>value <prefix>value ...}
     * @param prefixes   Prefixes to tokenize the arguments string with
     * @return           ArgumentMultimap object that maps prefixes to their arguments
     */
    public static ArgumentMultimap tokenize(String argsString, Prefix... prefixes) {
        List<PrefixPosition> positions = findAllPrefixPositions(argsString, prefixes);
        return extractArguments(argsString, positions);
    }

    /**
     * Strictly tokenizes hyphen-prefixed command options according to {@code options}.
     *
     * <p>Aliases are canonicalized to the {@link Prefix} held by their option. Unknown options,
     * duplicates, absent values, positional text, and values containing option-like tokens are
     * rejected with the same parser-level error.</p>
     *
     * @param argsString command arguments containing only configured options
     * @param options required, optional, and flag-only option specifications
     * @return the parsed options, keyed by their canonical prefixes
     * @throws ParseException if the input or option configuration is malformed
     */
    public static ArgumentMultimap tokenizeOptions(String argsString, Option... options) throws ParseException {
        if (argsString == null || options == null) {
            throw new NullPointerException("Arguments and options must not be null");
        }

        Map<String, Option> optionsByAlias = buildAliasMap(options);
        List<OptionOccurrence> occurrences = findOptionOccurrences(argsString, optionsByAlias);
        validateNoPositionalPreamble(argsString, occurrences);

        ArgumentMultimap result = new ArgumentMultimap();
        Set<Prefix> seenPrefixes = new HashSet<>();
        for (int i = 0; i < occurrences.size(); i++) {
            OptionOccurrence occurrence = occurrences.get(i);
            Option option = occurrence.option();
            if (!seenPrefixes.add(option.getPrefix())) {
                throw malformedOptions();
            }

            int valueEnd = i + 1 < occurrences.size()
                    ? occurrences.get(i + 1).startPosition()
                    : argsString.length();
            String value = argsString.substring(occurrence.endPosition(), valueEnd).trim();
            if ((option.getKind() == Kind.FLAG && !value.isEmpty())
                    || (option.getKind() != Kind.FLAG && value.isEmpty())) {
                throw malformedOptions();
            }
            result.put(option.getPrefix(), value);
        }

        boolean missingRequiredOption = Arrays.stream(options)
                .anyMatch(option -> option.getKind() == Kind.REQUIRED && !result.contains(option.getPrefix()));
        if (missingRequiredOption) {
            throw malformedOptions();
        }
        return result;
    }

    private static Map<String, Option> buildAliasMap(Option... options) {
        Map<String, Option> optionsByAlias = new HashMap<>();
        Set<Prefix> configuredPrefixes = new HashSet<>();
        for (Option option : options) {
            if (option == null || !configuredPrefixes.add(option.getPrefix())) {
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

    private static ParseException malformedOptions() {
        return new ParseException(MESSAGE_INVALID_COMMAND_OPTIONS);
    }

    private record OptionOccurrence(Option option, int startPosition, int endPosition) { }

    /**
     * Finds all zero-based prefix positions in the given arguments string.
     *
     * @param argsString Arguments string of the form: {@code preamble <prefix>value <prefix>value ...}
     * @param prefixes   Prefixes to find in the arguments string
     * @return           List of zero-based prefix positions in the given arguments string
     */
    private static List<PrefixPosition> findAllPrefixPositions(String argsString, Prefix... prefixes) {
        return Arrays.stream(prefixes)
                .flatMap(prefix -> findPrefixPositions(argsString, prefix).stream())
                .collect(Collectors.toList());
    }

    /**
     * @see #findAllPrefixPositions(String, Prefix...)
     */
    private static List<PrefixPosition> findPrefixPositions(String argsString, Prefix prefix) {
        List<PrefixPosition> positions = new ArrayList<>();

        int prefixPosition = findPrefixPosition(argsString, prefix.getPrefix(), 0);
        while (prefixPosition != -1) {
            PrefixPosition extendedPrefix = new PrefixPosition(prefix, prefixPosition);
            positions.add(extendedPrefix);
            prefixPosition = findPrefixPosition(argsString, prefix.getPrefix(), prefixPosition);
        }

        return positions;
    }

    /**
     * Returns the index of the first occurrence of {@code prefix} in
     * {@code argsString} starting from index {@code fromIndex}. An occurrence
     * is valid if there is a whitespace before {@code prefix}. Returns -1 if no
     * such occurrence can be found.
     *
     * E.g if {@code argsString} = "e/hip/900", {@code prefix} = "p/" and
     * {@code fromIndex} = 0, this method returns -1 as there are no valid
     * occurrences of "p/" with whitespace before it. However, if
     * {@code argsString} = "e/hi p/900", {@code prefix} = "p/" and
     * {@code fromIndex} = 0, this method returns 5.
     */
    private static int findPrefixPosition(String argsString, String prefix, int fromIndex) {
        int prefixIndex = argsString.indexOf(" " + prefix, fromIndex);
        return prefixIndex == -1 ? -1
                : prefixIndex + 1; // +1 as offset for whitespace
    }

    /**
     * Extracts prefixes and their argument values, and returns an {@code ArgumentMultimap} object that maps the
     * extracted prefixes to their respective arguments. Prefixes are extracted based on their zero-based positions in
     * {@code argsString}.
     *
     * @param argsString      Arguments string of the form: {@code preamble <prefix>value <prefix>value ...}
     * @param prefixPositions Zero-based positions of all prefixes in {@code argsString}
     * @return                ArgumentMultimap object that maps prefixes to their arguments
     */
    private static ArgumentMultimap extractArguments(String argsString, List<PrefixPosition> prefixPositions) {

        // Sort by start position
        prefixPositions.sort((prefix1, prefix2) -> prefix1.getStartPosition() - prefix2.getStartPosition());

        // Insert a PrefixPosition to represent the preamble
        PrefixPosition preambleMarker = new PrefixPosition(new Prefix(""), 0);
        prefixPositions.addFirst(preambleMarker);

        // Add a dummy PrefixPosition to represent the end of the string
        PrefixPosition endPositionMarker = new PrefixPosition(new Prefix(""), argsString.length());
        prefixPositions.add(endPositionMarker);

        // Map prefixes to their argument values (if any)
        ArgumentMultimap argMultimap = new ArgumentMultimap();
        for (int i = 0; i < prefixPositions.size() - 1; i++) {
            // Extract and store prefixes and their arguments
            Prefix argPrefix = prefixPositions.get(i).getPrefix();
            String argValue = extractArgumentValue(argsString, prefixPositions.get(i), prefixPositions.get(i + 1));
            argMultimap.put(argPrefix, argValue);
        }

        return argMultimap;
    }

    /**
     * Returns the trimmed value of the argument in the arguments string specified by {@code currentPrefixPosition}.
     * The end position of the value is determined by {@code nextPrefixPosition}.
     */
    private static String extractArgumentValue(String argsString,
                                        PrefixPosition currentPrefixPosition,
                                        PrefixPosition nextPrefixPosition) {
        Prefix prefix = currentPrefixPosition.getPrefix();

        int valueStartPos = currentPrefixPosition.getStartPosition() + prefix.getPrefix().length();
        String value = argsString.substring(valueStartPos, nextPrefixPosition.getStartPosition());

        return value.trim();
    }

    /**
     * Represents a prefix's position in an arguments string.
     */
    private static class PrefixPosition {
        private int startPosition;
        private final Prefix prefix;

        PrefixPosition(Prefix prefix, int startPosition) {
            this.prefix = prefix;
            this.startPosition = startPosition;
        }

        int getStartPosition() {
            return startPosition;
        }

        Prefix getPrefix() {
            return prefix;
        }
    }

}
