package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_APPLICANT;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.EditJobCommand;
import seedu.address.model.application.Job;

/**
 * Checks the edit-job syntax, aliases, and rejection of malformed input.
 */
public class EditJobCommandParserTest {

    private final EditJobCommandParser parser = new EditJobCommandParser();

    @Test
    public void parse_validArgs_returnsEditJobCommand() {
        EditJobCommand expected = new EditJobCommand(INDEX_FIRST_APPLICANT, new Job("Software Engineer Intern"));
        assertParseSuccess(parser, "1 -j Software Engineer Intern", expected);
        assertParseSuccess(parser, "  1   -job   Software Engineer Intern  ", expected);
        assertParseSuccess(parser, "1\t-j\tSoftware Engineer Intern", expected);
    }

    @Test
    public void parse_invalidIndex_throwsParseException() {
        for (String index : List.of("", "0", "-1", "1.5", "abc", "2147483648", "1 2")) {
            assertParseFailure(parser, index + " -j Engineer",
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, EditJobCommand.MESSAGE_USAGE));
        }
    }

    @Test
    public void parse_missingOrMalformedOptions_throwsParseException() {
        for (String args : List.of("", "1", "1 -j", "1 -job   ", "1 Engineer", "-j Engineer 1",
                "1 -j Engineer -s pending", "1 -j Engineer -unknown value", "1 -j Engineer -j Analyst",
                "1 -j Engineer -job Analyst", "1 -J Engineer")) {
            assertParseFailure(parser, args,
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, EditJobCommand.MESSAGE_USAGE));
        }
    }
}
