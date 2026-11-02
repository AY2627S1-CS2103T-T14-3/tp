package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.AddJobCommand;
import seedu.address.model.application.Job;

public class AddJobCommandParserTest {

    private final AddJobCommandParser parser = new AddJobCommandParser();

    @Test
    public void parse_unicodeOptionBoundaries_preservesRoleAndDescription() {
        AddJobCommand expected = new AddJobCommand(new Job("Engineer", "Original description"));
        assertParseSuccess(parser, " -j Engineer\u2003-d Original description", expected);

        for (String whitespace : List.of("\u2003", "\u2009", "\u3000")) {
            assertParseSuccess(parser, whitespace + "-j" + whitespace + "Engineer" + whitespace
                    + "-d" + whitespace + "Original description", expected);
        }
    }

    @Test
    public void parse_validRoleNoDesc_returnsAddJobCommand() {
        assertParseSuccess(parser, " -j Software Engineer Intern",
                new AddJobCommand(new Job("Software Engineer Intern")));
        assertParseSuccess(parser, " -j Data Analyst",
                new AddJobCommand(new Job("Data Analyst")));
    }

    @Test
    public void parse_validRoleWithDesc_returnsAddJobCommand() {
        assertParseSuccess(parser, " -j Software Engineer Intern -d Yeehaw",
                new AddJobCommand(new Job("Software Engineer Intern", "Yeehaw")));
        assertParseSuccess(parser, " -j Data Analyst -d to be replaced by ai",
                new AddJobCommand(new Job("Data Analyst", "to be replaced by ai")));
    }

    @Test
    public void parse_validRoleWithInvalidDesc_returnsAddJobCommand() {
        String expected = String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddJobCommand.MESSAGE_USAGE);
        assertParseFailure(parser, " -j Intern -d", expected);
    }

    @Test
    public void parse_longUnknownOption_throwsParseException() {
        String expected = String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddJobCommand.MESSAGE_USAGE);
        assertParseFailure(parser, " -" + "a-".repeat(2000) + "a value", expected);
    }

    @Test
    public void parse_invalidRole_throwsParseException() {
        String expected = String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddJobCommand.MESSAGE_USAGE);
        assertParseFailure(parser, "", expected);
        assertParseFailure(parser, " -j", expected);
    }
}
