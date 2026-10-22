package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.ViewJobCommand;

public class ViewJobCommandParserTest {

    private final ViewJobCommandParser parser = new ViewJobCommandParser();

    @Test
    public void parse_emptyArgs_returnsViewJobCommand() {
        assertParseSuccess(parser, "   ", new ViewJobCommand());
    }

    @Test
    public void parse_filter_returnsFilteredViewJobCommand() {
        assertParseSuccess(parser, " -f jan", new ViewJobCommand("jan"));
        assertParseSuccess(parser, " -filter Janitor", new ViewJobCommand("Janitor"));
    }

    @Test
    public void parse_invalidArgs_throwsParseException() {
        String expected = String.format(MESSAGE_INVALID_COMMAND_FORMAT, ViewJobCommand.MESSAGE_USAGE);
        assertParseFailure(parser, " 1", expected);
        assertParseFailure(parser, " -f", expected);
    }
}
