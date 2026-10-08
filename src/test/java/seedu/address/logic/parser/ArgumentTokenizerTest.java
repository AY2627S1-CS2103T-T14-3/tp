package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.parser.ArgumentTokenizer.MESSAGE_INVALID_COMMAND_OPTIONS;

import org.junit.jupiter.api.Test;

import seedu.address.logic.parser.exceptions.ParseException;

public class ArgumentTokenizerTest {

    private final Prefix unknownPrefix = new Prefix("--u");
    private final Prefix pSlash = new Prefix("p/");
    private final Prefix dashT = new Prefix("-t");
    private final Prefix hatQ = new Prefix("^Q");
    private final Prefix nameOption = new Prefix("name");
    private final Prefix emailOption = new Prefix("email");
    private final Prefix applicantFlag = new Prefix("applicant");

    @Test
    public void tokenize_emptyArgsString_noValues() {
        String argsString = "  ";
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(argsString, pSlash);

        assertPreambleEmpty(argMultimap);
        assertArgumentAbsent(argMultimap, pSlash);
    }

    private void assertPreamblePresent(ArgumentMultimap argMultimap, String expectedPreamble) {
        assertEquals(expectedPreamble, argMultimap.getPreamble());
    }

    private void assertPreambleEmpty(ArgumentMultimap argMultimap) {
        assertTrue(argMultimap.getPreamble().isEmpty());
    }

    /**
     * Asserts all the arguments in {@code argMultimap} with {@code prefix} match the {@code expectedValues}
     * and only the last value is returned upon calling {@code ArgumentMultimap#getValue(Prefix)}.
     */
    private void assertArgumentPresent(ArgumentMultimap argMultimap, Prefix prefix, String... expectedValues) {

        // Verify the last value is returned
        assertEquals(expectedValues[expectedValues.length - 1], argMultimap.getValue(prefix).get());

        // Verify the number of values returned is as expected
        assertEquals(expectedValues.length, argMultimap.getAllValues(prefix).size());

        // Verify all values returned are as expected and in order
        for (int i = 0; i < expectedValues.length; i++) {
            assertEquals(expectedValues[i], argMultimap.getAllValues(prefix).get(i));
        }
    }

    private void assertArgumentAbsent(ArgumentMultimap argMultimap, Prefix prefix) {
        assertFalse(argMultimap.getValue(prefix).isPresent());
    }

    @Test
    public void tokenize_noPrefixes_allTakenAsPreamble() {
        String argsString = "  some random string /t tag with leading and trailing spaces ";
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(argsString);

        // Same string expected as preamble, but leading/trailing spaces should be trimmed
        assertPreamblePresent(argMultimap, argsString.trim());

    }

    @Test
    public void tokenize_oneArgument() {
        // Preamble present
        String argsString = "  Some preamble string p/ Argument value ";
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(argsString, pSlash);
        assertPreamblePresent(argMultimap, "Some preamble string");
        assertArgumentPresent(argMultimap, pSlash, "Argument value");

        // No preamble
        argsString = " p/   Argument value ";
        argMultimap = ArgumentTokenizer.tokenize(argsString, pSlash);
        assertPreambleEmpty(argMultimap);
        assertArgumentPresent(argMultimap, pSlash, "Argument value");

    }

    @Test
    public void tokenize_multipleArguments() {
        // Only two arguments are present
        String argsString = "SomePreambleString -t dashT-Value p/pSlash value";
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(argsString, pSlash, dashT, hatQ);
        assertPreamblePresent(argMultimap, "SomePreambleString");
        assertArgumentPresent(argMultimap, pSlash, "pSlash value");
        assertArgumentPresent(argMultimap, dashT, "dashT-Value");
        assertArgumentAbsent(argMultimap, hatQ);

        // All three arguments are present
        argsString = "Different Preamble String ^Q111 -t dashT-Value p/pSlash value";
        argMultimap = ArgumentTokenizer.tokenize(argsString, pSlash, dashT, hatQ);
        assertPreamblePresent(argMultimap, "Different Preamble String");
        assertArgumentPresent(argMultimap, pSlash, "pSlash value");
        assertArgumentPresent(argMultimap, dashT, "dashT-Value");
        assertArgumentPresent(argMultimap, hatQ, "111");

        /* Also covers: Reusing of the tokenizer multiple times */

        // Reuse tokenizer on an empty string to ensure ArgumentMultimap is correctly reset
        // (i.e. no stale values from the previous tokenizing remain)
        argsString = "";
        argMultimap = ArgumentTokenizer.tokenize(argsString, pSlash, dashT, hatQ);
        assertPreambleEmpty(argMultimap);
        assertArgumentAbsent(argMultimap, pSlash);

        /* Also covers: testing for prefixes not specified as a prefix */

        // Prefixes not previously given to the tokenizer should not return any values
        argsString = unknownPrefix + "some value";
        argMultimap = ArgumentTokenizer.tokenize(argsString, pSlash, dashT, hatQ);
        assertArgumentAbsent(argMultimap, unknownPrefix);
        assertPreamblePresent(argMultimap, argsString); // Unknown prefix is taken as part of preamble
    }

    @Test
    public void tokenize_multipleArgumentsWithRepeats() {
        // Two arguments repeated, some have empty values
        String argsString = "SomePreambleString -t dashT-Value ^Q ^Q -t another dashT value p/ pSlash value -t";
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(argsString, pSlash, dashT, hatQ);
        assertPreamblePresent(argMultimap, "SomePreambleString");
        assertArgumentPresent(argMultimap, pSlash, "pSlash value");
        assertArgumentPresent(argMultimap, dashT, "dashT-Value", "another dashT value", "");
        assertArgumentPresent(argMultimap, hatQ, "", "");
    }

    @Test
    public void tokenize_multipleArgumentsJoined() {
        String argsString = "SomePreambleStringp/ pSlash joined-tjoined -t not joined^Qjoined";
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(argsString, pSlash, dashT, hatQ);
        assertPreamblePresent(argMultimap, "SomePreambleStringp/ pSlash joined-tjoined");
        assertArgumentAbsent(argMultimap, pSlash);
        assertArgumentPresent(argMultimap, dashT, "not joined^Qjoined");
        assertArgumentAbsent(argMultimap, hatQ);
    }

    @Test
    public void equalsMethod() {
        Prefix aaa = new Prefix("aaa");

        assertEquals(aaa, aaa);
        assertEquals(aaa, new Prefix("aaa"));

        assertNotEquals(aaa, "aaa");
        assertNotEquals(aaa, new Prefix("aab"));
    }

    @Test
    public void tokenizeOptions_shortAndLongAliases_valuesCanonicalized() throws ParseException {
        ArgumentMultimap shortOptions = ArgumentTokenizer.tokenizeOptions(
                " -n John Doe -e john@example.com",
                Option.required(nameOption, "-n", "-name"),
                Option.required(emailOption, "-e", "-email"));
        ArgumentMultimap longOptions = ArgumentTokenizer.tokenizeOptions(
                " -name John Doe -email john@example.com",
                Option.required(nameOption, "-n", "-name"),
                Option.required(emailOption, "-e", "-email"));

        assertArgumentPresent(shortOptions, nameOption, "John Doe");
        assertArgumentPresent(shortOptions, emailOption, "john@example.com");
        assertArgumentPresent(longOptions, nameOption, "John Doe");
        assertArgumentPresent(longOptions, emailOption, "john@example.com");
        assertArgumentAbsent(longOptions, new Prefix("-name"));
    }

    @Test
    public void tokenizeOptions_optionalOptionMayBeAbsent() throws ParseException {
        ArgumentMultimap options = ArgumentTokenizer.tokenizeOptions(
                "-n John Doe",
                Option.required(nameOption, "-n", "-name"),
                Option.optional(emailOption, "-e", "-email"));

        assertArgumentPresent(options, nameOption, "John Doe");
        assertArgumentAbsent(options, emailOption);
    }

    @Test
    public void tokenizeOptions_flagOption_presentWithoutValue() throws ParseException {
        ArgumentMultimap options = ArgumentTokenizer.tokenizeOptions(
                "-applicant -n John Doe",
                Option.flag(applicantFlag, "-applicant"),
                Option.required(nameOption, "-n", "-name"));

        assertTrue(options.contains(applicantFlag));
        assertEquals("", options.getValue(applicantFlag).orElseThrow());
    }

    @Test
    public void tokenizeOptions_duplicateMixedAliases_throwsParseException() {
        assertMalformedOptions("-n John -name Jane",
                Option.required(nameOption, "-n", "-name"));
    }

    @Test
    public void tokenizeOptions_missingValue_throwsParseException() {
        assertMalformedOptions("-n -e john@example.com",
                Option.required(nameOption, "-n", "-name"),
                Option.required(emailOption, "-e", "-email"));
        assertMalformedOptions("-n John -e",
                Option.required(nameOption, "-n", "-name"),
                Option.required(emailOption, "-e", "-email"));
    }

    @Test
    public void tokenizeOptions_unknownOption_throwsParseException() {
        assertMalformedOptions("-n John Doe -unknown value",
                Option.required(nameOption, "-n", "-name"));
    }

    @Test
    public void tokenizeOptions_unexpectedPositionalText_throwsParseException() {
        assertMalformedOptions("unexpected -n John Doe",
                Option.required(nameOption, "-n", "-name"));
        assertMalformedOptions("-applicant unexpected",
                Option.flag(applicantFlag, "-applicant"));
    }

    @Test
    public void tokenizeOptions_missingRequiredOption_throwsParseException() {
        assertMalformedOptions("-e john@example.com",
                Option.required(nameOption, "-n", "-name"),
                Option.optional(emailOption, "-e", "-email"));
    }

    @Test
    public void tokenizeOptions_optionLikeTextInsideValue_throwsParseException() {
        assertMalformedOptions("-n John -nickname Doe",
                Option.required(nameOption, "-n", "-name"));
    }

    @Test
    public void option_invalidOrDuplicateAliases_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> Option.required(nameOption));
        assertThrows(IllegalArgumentException.class, () -> Option.required(nameOption, "name"));
        assertThrows(IllegalArgumentException.class, () -> Option.required(nameOption, "-n", "-n"));
    }

    @Test
    public void tokenizeOptions_conflictingConfiguration_throwsIllegalArgumentException() {
        Option name = Option.required(nameOption, "-n", "-name");
        Option emailWithConflictingAlias = Option.required(emailOption, "-e", "-name");

        assertThrows(IllegalArgumentException.class, () ->
                ArgumentTokenizer.tokenizeOptions("", name, emailWithConflictingAlias));
        assertThrows(IllegalArgumentException.class, () ->
                ArgumentTokenizer.tokenizeOptions("", name, name));
    }

    private void assertMalformedOptions(String argsString, Option... options) {
        ParseException exception = assertThrows(ParseException.class, () ->
                ArgumentTokenizer.tokenizeOptions(argsString, options));
        assertEquals(MESSAGE_INVALID_COMMAND_OPTIONS, exception.getMessage());
    }

}
