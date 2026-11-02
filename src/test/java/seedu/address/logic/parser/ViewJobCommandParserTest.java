package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.ViewJobCommand;

public class ViewJobCommandParserTest {

    private final ViewJobCommandParser parser = new ViewJobCommandParser();

    @Test
    public void parse_emptyArgs_returnsViewJobCommand() {
        assertParseSuccess(parser, "", new ViewJobCommand());
        assertParseSuccess(parser, "   ", new ViewJobCommand());
    }

    @Test
    public void parse_filter_returnsFilteredViewJobCommand() {
        assertParseSuccess(parser, " -f jan", new ViewJobCommand("jan"));
        assertParseSuccess(parser, " -filter Janitor", new ViewJobCommand("Janitor"));
        assertParseSuccess(parser, " -f software engineer", new ViewJobCommand("software engineer"));
    }

    @Test
    public void parse_index_returnsSelectionCommand() {
        assertParseSuccess(parser, "1", new ViewJobCommand(INDEX_FIRST));
        assertParseSuccess(parser, " \t1  ", new ViewJobCommand(INDEX_FIRST));
    }

    @Test
    public void parse_longUnknownOption_throwsParseException() {
        String expected = String.format(MESSAGE_INVALID_COMMAND_FORMAT, ViewJobCommand.MESSAGE_USAGE);
        for (String preamble : List.of("", "1 ", "-f jan ")) {
            assertParseFailure(parser, preamble + "-" + "a-".repeat(2000) + "a value", expected);
        }
    }

    @Test
    public void parse_invalidArgs_throwsParseException() {
        String expected = String.format(MESSAGE_INVALID_COMMAND_FORMAT, ViewJobCommand.MESSAGE_USAGE);
        for (String args : List.of("0", "-1", "1.5", "2147483648", "abc", "1 2", "-f",
                "1 -f jan", "1 -filter jan", "-f jan -filter engineer", "-unknown jan")) {
            assertParseFailure(parser, args, expected);
        }
    }
}
