package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST;
import static seedu.address.testutil.TypicalIndexes.INDEX_SECOND;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.ViewApplicationCommand;

/** Tests parsing application views with an optional applicant index. */
public class ViewApplicationCommandParserTest {

    private final ViewApplicationCommandParser parser = new ViewApplicationCommandParser();

    @Test
    public void parse_emptyArgs_returnsViewApplicationCommandWithoutIndex() {
        assertParseSuccess(parser, "", new ViewApplicationCommand());
        assertParseSuccess(parser, "  \t  ", new ViewApplicationCommand());
    }

    @Test
    public void parse_validIndex_returnsViewApplicationCommand() {
        assertParseSuccess(parser, "1", new ViewApplicationCommand(INDEX_FIRST));
        assertParseSuccess(parser, "  \t2  ", new ViewApplicationCommand(INDEX_SECOND));
    }

    @Test
    public void parse_invalidArgs_throwsParseException() {
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, ViewApplicationCommand.MESSAGE_USAGE);
        for (String args : List.of("0", "-1", "+1", "1.5", "abc", "2147483648", "1 2", "1 extra", "-p 1")) {
            assertParseFailure(parser, args, expectedMessage);
        }
    }
}
