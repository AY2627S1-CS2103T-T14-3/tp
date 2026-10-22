package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.Messages.MESSAGE_DUPLICATE_FIELDS;
import static seedu.address.logic.parser.ArgumentTokenizer.HAS_PREAMBLE;
import static seedu.address.logic.parser.ArgumentTokenizer.NO_PREAMBLE;

import org.junit.jupiter.api.Test;

import seedu.address.logic.parser.exceptions.ParseException;

public class ArgumentTokenizerTest {

    private final Option nameOption = Option.required("-n", "-name");
    private final Option emailOption = Option.required("-e", "-email");
    private final Option unusedOption = Option.optional("-u", "-unused");
    private final Option testFlag = Option.flag("-t");

    /**
     * Asserts all the arguments in {@code argMultimap} with {@code prefix} match the {@code expectedValues}
     * and only the last value is returned upon calling {@code ArgumentMultimap#getValue(Prefix)}.
     */
    private void assertArgumentPresent(ArgumentMultimap argMultimap, Option option, String... expectedValues) {

        // Verify the last value is returned
        assertEquals(expectedValues[expectedValues.length - 1], argMultimap.getValue(option).get());

        // Verify the number of values returned is as expected
        assertEquals(expectedValues.length, argMultimap.getAllValues(option).size());

        // Verify all values returned are as expected and in order
        for (int i = 0; i < expectedValues.length; i++) {
            assertEquals(expectedValues[i], argMultimap.getAllValues(option).get(i));
        }
    }

    private void assertArgumentAbsent(ArgumentMultimap argMultimap, Option option) {
        assertFalse(argMultimap.getValue(option).isPresent());
    }

    @Test
    public void tokenizeOptions_shortAndLongAliases_valuesCanonicalized() throws ParseException {
        ArgumentMultimap shortOptions = ArgumentTokenizer.tokenizeOptions(
                " -n John Doe -e john@example.com",
                NO_PREAMBLE,
                nameOption,
                emailOption);
        ArgumentMultimap longOptions = ArgumentTokenizer.tokenizeOptions(
                " -name John Doe -email john@example.com",
                HAS_PREAMBLE,
                nameOption,
                emailOption);

        assertArgumentPresent(shortOptions, nameOption, "John Doe");
        assertArgumentPresent(shortOptions, emailOption, "john@example.com");
        assertArgumentPresent(longOptions, nameOption, "John Doe");
        assertArgumentPresent(longOptions, emailOption, "john@example.com");
        assertArgumentAbsent(longOptions, testFlag);
    }

    @Test
    public void tokenizeOptions_optionalOptionMayBeAbsent() throws ParseException {
        ArgumentMultimap options = ArgumentTokenizer.tokenizeOptions(
                "-n John Doe",
                NO_PREAMBLE,
                nameOption,
                unusedOption);

        assertArgumentPresent(options, nameOption, "John Doe");
        assertArgumentAbsent(options, unusedOption);
    }

    @Test
    public void tokenizeOptions_flagOption_presentWithoutValue() throws ParseException {
        ArgumentMultimap options = ArgumentTokenizer.tokenizeOptions(
                "-t -n John Doe",
                NO_PREAMBLE,
                testFlag,
                nameOption);

        assertTrue(options.contains(testFlag));
        assertEquals("", options.getValue(testFlag).orElseThrow());
    }

    @Test
    public void tokenizeOptions_duplicateMixedAliases_throwsParseException() {
        assertMultipleOptions("-n John -name Jane", "-n", nameOption);
    }

    @Test
    public void tokenizeOptions_missingValue_throwsParseException() {
        assertMalformedOptions("-n -e john@example.com",
                nameOption,
                emailOption);
        assertMalformedOptions("-n John -e",
                nameOption,
                emailOption);
    }

    @Test
    public void tokenizeOptions_unknownOption_throwsParseException() {
        assertMalformedOptions("-n John Doe -unknown value",
                nameOption);
    }

    @Test
    public void tokenizeOptions_unexpectedPositionalText_throwsParseException() {
        assertMalformedOptions("unexpected -n John Doe",
                nameOption);
        assertMalformedOptions("-t unexpected",
                testFlag);
    }

    @Test
    public void tokenizeOptions_missingRequiredOption_throwsParseException() {
        assertMalformedOptions("-e john@example.com",
                nameOption,
                emailOption);
    }

    @Test
    public void tokenizeOptions_optionLikeTextInsideValue_throwsParseException() {
        assertMalformedOptions("-n John -nickname Doe",
                nameOption,
                emailOption);
    }

    @Test
    public void option_invalidOrDuplicateAliases_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, Option::required);
        assertThrows(IllegalArgumentException.class, () -> Option.required("name"));
        assertThrows(IllegalArgumentException.class, () -> Option.required("-n", "-n"));
    }

    @Test
    public void tokenizeOptions_conflictingConfiguration_throwsIllegalArgumentException() {
        Option name = Option.required("-n", "-name");
        Option emailWithConflictingAlias = Option.required("-e", "-name");

        assertThrows(IllegalArgumentException.class, () ->
                ArgumentTokenizer.tokenizeOptions("", NO_PREAMBLE, name, emailWithConflictingAlias));
        assertThrows(IllegalArgumentException.class, () ->
                ArgumentTokenizer.tokenizeOptions("", NO_PREAMBLE, name, name));
    }

    private void assertMalformedOptions(String argsString, Option... options) {
        ParseException exception = assertThrows(ParseException.class, () ->
                ArgumentTokenizer.tokenizeOptions(argsString, NO_PREAMBLE, options));
        assertEquals(MESSAGE_INVALID_COMMAND_OPTIONS, exception.getMessage());
    }

    private void assertMultipleOptions(String argsString, String multipleOptions, Option... options) {
        ParseException exception = assertThrows(ParseException.class, () ->
                ArgumentTokenizer.tokenizeOptions(argsString, NO_PREAMBLE, options));
        assertEquals(MESSAGE_DUPLICATE_FIELDS + multipleOptions, exception.getMessage());
    }

}
