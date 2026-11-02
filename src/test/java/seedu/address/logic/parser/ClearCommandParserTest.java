package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.ClearCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/** Tests that clearing accepts only blank arguments. */
public class ClearCommandParserTest {

    private final ClearCommandParser parser = new ClearCommandParser();

    @Test
    public void parse_emptyArgs_returnsClearCommand() throws ParseException {
        assertNotNull(parser.parse(""));
        assertNotNull(parser.parse("  \t  "));
    }

    @Test
    public void parse_nonemptyArgs_throwsParseException() {
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, ClearCommand.MESSAGE_USAGE);
        for (String args : List.of("applicants", "applications", "jobs", "3", "-p 1", "  extra  ")) {
            assertParseFailure(parser, args, expectedMessage);
        }
    }
}
