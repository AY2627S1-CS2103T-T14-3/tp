package seedu.address.logic.parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import seedu.address.logic.Messages;
import seedu.address.logic.parser.exceptions.ParseException;

import static seedu.address.logic.parser.CliSyntax.OPTION_EMPTY;

/**
 * Stores mapping of options to their respective arguments.
 * Each key may be associated with multiple argument values.
 * Values for a given key are stored in a list, and the insertion ordering is maintained.
 * Keys are unique, but the list of argument values may contain duplicate argument values, i.e. the same argument value
 * can be inserted multiple times for the same option.
 */
public class ArgumentMultimap {

    /** Options mapped to their respective arguments */
    private final Map<Option, List<String>> argMultimap = new HashMap<>();

    /**
     * Associates the specified argument value with {@code option} key in this map.
     * If the map previously contained a mapping for the key, the new value is appended to the list of existing values.
     *
     * @param option   Options key with which the specified argument value is to be associated
     * @param argValue Argument value to be associated with the specified option key
     */
    public void put(Option option, String argValue) {
        List<String> argValues = getAllValues(option);
        argValues.add(argValue);
        argMultimap.put(option, argValues);
    }

    /**
     * Returns the last value of {@code option}.
     */
    public Optional<String> getValue(Option option) {
        List<String> values = getAllValues(option);
        return values.isEmpty() ? Optional.empty() : Optional.of(values.getLast());
    }

    /**
     * Returns all values of {@code option}.
     * If the option does not exist or has no values, this will return an empty list.
     * Modifying the returned list will not affect the underlying data structure of the ArgumentMultimap.
     */
    public List<String> getAllValues(Option option) {
        if (!argMultimap.containsKey(option)) {
            return new ArrayList<>();
        }
        return new ArrayList<>(argMultimap.get(option));
    }

    /** Returns true if {@code option} was present, including when it was a flag. */
    public boolean contains(Option option) {
        return argMultimap.containsKey(option);
    }

    /**
     * Returns the preamble (text before the first valid option). Trims any leading/trailing spaces.
     */
    public String getPreamble() {
        return getValue(OPTION_EMPTY).orElse("");
    }

    /**
     * Throws a {@code ParseException} if any of the options given in {@code options} appeared more than
     * once among the arguments.
     */
    public void verifyNoDuplicateOptionsFor(Option... option) throws ParseException {
        Option[] duplicatedOptions = Stream.of(option).distinct()
                .filter(prefix -> argMultimap.containsKey(prefix) && argMultimap.get(prefix).size() > 1)
                .toArray(Option[]::new);

        if (duplicatedOptions.length > 0) {
            throw new ParseException(Messages.getErrorMessageForDuplicateOptions(duplicatedOptions));
        }
    }
}
