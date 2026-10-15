package seedu.address.logic.parser;

import static seedu.address.logic.commands.AddApplicationCommand.MESSAGE_INVALID_STATUS;
import static seedu.address.logic.commands.AddApplicationCommand.MESSAGE_UNRECOGNIZED;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.address.testutil.TypicalIndexes.INDEX_SECOND_PERSON;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.AddApplicationCommand;
import seedu.address.model.application.Application;

public class AddApplicationCommandParserTest {

    private final AddApplicationCommandParser parser = new AddApplicationCommandParser();

    @Test
    public void parse_allFields_returnsCommand() {
        AddApplicationCommand expected = new AddApplicationCommand(
                INDEX_SECOND_PERSON, "ClaudeGPT Expert", Application.Status.ACCEPT);
        assertParseSuccess(parser, " -p 2 -j ClaudeGPT Expert -s accept", expected);
        assertParseSuccess(parser, " -application 2 -job ClaudeGPT Expert -status accept", expected);
        assertParseSuccess(parser, " -s accept -job ClaudeGPT Expert -p 2", expected);
    }

    @Test
    public void parse_optionalStatusMissing_defaultsToPending() {
        AddApplicationCommand expected = new AddApplicationCommand(
                INDEX_FIRST_PERSON, "full-time engineer", Application.Status.PENDING);
        assertParseSuccess(parser, " -p 1 -j full-time engineer", expected);
    }

    @Test
    public void parse_invalidStatus_throwsParseException() {
        assertParseFailure(parser, " -p 1 -j Janitor -s ACCEPT", MESSAGE_INVALID_STATUS);
        assertParseFailure(parser, " -p 1 -j Janitor -s Pending", MESSAGE_INVALID_STATUS);
        assertParseFailure(parser, " -p 1 -j Janitor -status accepted", MESSAGE_INVALID_STATUS);
    }

    @Test
    public void parse_invalidFormat_throwsParseException() {
        assertUnrecognized(" -p 1");
        assertUnrecognized(" -j Janitor");
        assertUnrecognized(" -p 0 -j Janitor");
        assertUnrecognized(" -p one -j Janitor");
        assertUnrecognized(" preamble -p 1 -j Janitor");
        assertUnrecognized(" -p 1 -j Janitor -s accept -j Other");
        assertUnrecognized(" -p 1 -j Janitor -level role");
        assertUnrecognized(" -x 1 -j Janitor");
    }

    private void assertUnrecognized(String args) {
        String input = ("add" + args).trim().replaceAll("\\s+", " ");
        assertParseFailure(parser, args, String.format(MESSAGE_UNRECOGNIZED, input));
    }
}
