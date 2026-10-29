package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.Messages.MESSAGE_INVALID_JOB_ROLE;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.AddJobCommand;
import seedu.address.model.application.Job;

public class AddJobCommandParserTest {

    private final AddJobCommandParser parser = new AddJobCommandParser();

    @Test
    public void parse_validRoleNoDesc_returnsAddJobCommand() {
        assertParseSuccess(parser, " Software Engineer Intern",
                new AddJobCommand(new Job("Software Engineer Intern")));
        assertParseSuccess(parser, " Data Analyst",
                new AddJobCommand(new Job("Data Analyst")));
    }

    @Test
    public void parse_validRoleWithDesc_returnsAddJobCommand() {
        assertParseSuccess(parser, " Software Engineer Intern -j Yeehaw",
                new AddJobCommand(new Job("Software Engineer Intern", "Yeehaw")));
        assertParseSuccess(parser, " Data Analyst -j to be replaced by ai",
                new AddJobCommand(new Job("Data Analyst", "to be replaced by ai")));
    }

    @Test
    public void parse_validRoleWithInvalidDesc_returnsAddJobCommand() {
        String expected = String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddJobCommand.MESSAGE_USAGE);
        assertParseFailure(parser, " Intern -j", expected);
    }

    @Test
    public void parse_invalidRole_throwsParseException() {
        String expected = String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddJobCommand.MESSAGE_USAGE);
        assertParseFailure(parser, "", MESSAGE_INVALID_JOB_ROLE);
        assertParseFailure(parser, " -j", expected);
    }
}
