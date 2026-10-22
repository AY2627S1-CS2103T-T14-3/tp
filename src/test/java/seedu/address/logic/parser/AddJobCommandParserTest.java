package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.AddJobCommand;
import seedu.address.model.application.Job;

public class AddJobCommandParserTest {

    private final AddJobCommandParser parser = new AddJobCommandParser();

    @Test
    public void parse_validArgs_returnsAddJobCommand() {
        assertParseSuccess(parser, " -j Software Engineer Intern",
                new AddJobCommand(new Job("Software Engineer Intern")));
        assertParseSuccess(parser, " -job Data Analyst",
                new AddJobCommand(new Job("Data Analyst")));
    }

    @Test
    public void parse_missingDescription_throwsParseException() {
        String expected = String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddJobCommand.MESSAGE_USAGE);
        assertParseFailure(parser, "", expected);
        assertParseFailure(parser, " -j", expected);
        assertParseFailure(parser, " Software Engineer", expected);
    }
}
