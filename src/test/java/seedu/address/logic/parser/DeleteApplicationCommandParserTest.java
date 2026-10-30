package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.DeleteApplicationCommand;

public class DeleteApplicationCommandParserTest {

    private final DeleteApplicationCommandParser parser = new DeleteApplicationCommandParser();

    @Test
    public void parse_validArgs_returnsDeleteApplicationCommand() {
        assertParseSuccess(parser, "1", new DeleteApplicationCommand(INDEX_FIRST));
        assertParseSuccess(parser, " \t1 \t", new DeleteApplicationCommand(INDEX_FIRST));
    }

    @Test
    public void parse_invalidArgs_throwsParseException() {
        for (String args : List.of("", "a", "0", "-1", "1.5", "2147483648", "1 2", "1 -j Janitor", "-p 1")) {
            assertParseFailure(parser, args,
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, DeleteApplicationCommand.MESSAGE_USAGE));
        }
    }
}
