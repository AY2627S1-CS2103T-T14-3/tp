package seedu.address.logic.parser;

import static seedu.address.logic.commands.SetApplicationStatusCommand.MESSAGE_INVALID_STATUS;
import static seedu.address.logic.commands.SetApplicationStatusCommand.MESSAGE_UNRECOGNIZED;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.testutil.TypicalIndexes.INDEX_SECOND_PERSON;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.SetApplicationStatusCommand;
import seedu.address.model.application.Application;

public class SetApplicationStatusCommandParserTest {

    private final SetApplicationStatusCommandParser parser = new SetApplicationStatusCommandParser();

    @Test
    public void parse_allFields_returnsCommand() {
        SetApplicationStatusCommand expected = new SetApplicationStatusCommand(
                INDEX_SECOND_PERSON, Application.Status.ACCEPT);
        assertParseSuccess(parser, " 2 -s accept", expected);
        assertParseSuccess(parser, " 2 -status accept", expected);
    }

    @Test
    public void parse_invalidStatus_throwsParseException() {
        assertParseFailure(parser, " 1 -s ACCEPT", MESSAGE_INVALID_STATUS);
        assertParseFailure(parser, " 1 -s Pending", MESSAGE_INVALID_STATUS);
        assertParseFailure(parser, " 1 -status accepted", MESSAGE_INVALID_STATUS);
        assertParseFailure(parser, " 1 -s accept extra", MESSAGE_INVALID_STATUS);
    }

    @Test
    public void parse_invalidFormat_throwsParseException() {
        assertUnrecognized("");
        assertUnrecognized(" 1");
        assertUnrecognized(" -s accept");
        assertUnrecognized(" -s accept 2");
        assertUnrecognized(" 0 -s accept");
        assertUnrecognized(" one -s accept");
        assertUnrecognized(" preamble 1 -s accept");
        assertUnrecognized(" 1 -s accept -s reject");
        assertUnrecognized(" 1 -s accept -j Janitor");
        assertUnrecognized(" -x 1 -s accept");
        assertUnrecognized(" -p 2 -s accept");
        assertUnrecognized(" -application 2 -status accept");
    }

    private void assertUnrecognized(String args) {
        String input = ("set" + args).trim().replaceAll("\\s+", " ");
        assertParseFailure(parser, args, String.format(MESSAGE_UNRECOGNIZED, input));
    }
}
