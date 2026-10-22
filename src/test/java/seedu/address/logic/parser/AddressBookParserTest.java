package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.Messages.MESSAGE_UNKNOWN_COMMAND;
import static seedu.address.logic.commands.AddApplicationCommand.MESSAGE_INVALID_STATUS;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;

import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.AddApplicationCommand;
import seedu.address.logic.commands.AddCommand;
import seedu.address.logic.commands.AddJobCommand;
import seedu.address.logic.commands.ByeCommand;
import seedu.address.logic.commands.ClearCommand;
import seedu.address.logic.commands.DeleteCommand;
import seedu.address.logic.commands.DeleteJobCommand;
import seedu.address.logic.commands.FindCommand;
import seedu.address.logic.commands.HelpCommand;
import seedu.address.logic.commands.ListCommand;
import seedu.address.logic.commands.SetApplicationStatusCommand;
import seedu.address.logic.commands.ViewCommand;
import seedu.address.logic.commands.ViewJobCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.application.Application;
import seedu.address.model.application.Job;
import seedu.address.model.person.NameContainsKeywordsPredicate;
import seedu.address.model.person.Person;
import seedu.address.testutil.PersonBuilder;
import seedu.address.testutil.PersonUtil;

public class AddressBookParserTest {

    private final AddressBookParser parser = new AddressBookParser();

    @Test
    public void parseCommand_bye() throws Exception {
        assertTrue(parser.parseCommand("bye") instanceof ByeCommand);
        assertTrue(parser.parseCommand(" \tbye \t") instanceof ByeCommand);
        assertTrue(parser.parseCommand("exit") instanceof ByeCommand);
        assertTrue(parser.parseCommand(" \texit \t") instanceof ByeCommand);
    }

    @Test
    public void parseCommand_byeWithArguments_throwsParseException() {
        for (String input : List.of("bye 3", "bye -p 1", "bye -applicant",
                "exit 3", "exit -p 1", "exit -applicant")) {
            assertThrows(ParseException.class,
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, ByeCommand.MESSAGE_USAGE), ()
                        -> parser.parseCommand(input));
        }
    }

    @Test
    public void parseCommand_byeWithIncorrectCase_throwsParseException() {
        for (String input : List.of("Bye", "BYE", "Exit", "EXIT")) {
            assertThrows(ParseException.class, MESSAGE_UNKNOWN_COMMAND, () -> parser.parseCommand(input));
        }
    }

    @Test
    public void parseCommand_view() throws Exception {
        assertEquals(new ViewCommand(INDEX_FIRST_PERSON), parser.parseCommand("view 1"));
        assertEquals(new ViewCommand(INDEX_FIRST_PERSON), parser.parseCommand("  view   1  "));
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, ViewCommand.MESSAGE_USAGE);
        assertThrows(ParseException.class, expectedMessage, () -> parser.parseCommand("view"));
        assertThrows(ParseException.class, MESSAGE_UNKNOWN_COMMAND, () -> parser.parseCommand("View 1"));
    }

    @Test
    public void parseCommand_add() throws Exception {
        Person person = new PersonBuilder().build();
        AddCommand command = (AddCommand) parser.parseCommand(PersonUtil.getAddCommand(person));
        assertEquals(new AddCommand(person), command);
    }

    @Test
    public void parseCommand_addApplication() throws Exception {
        AddApplicationCommand expected = new AddApplicationCommand(
                INDEX_FIRST_PERSON, "Janitor", Application.Status.PENDING);
        assertEquals(expected, parser.parseCommand("addp 1 -j Janitor"));
        assertEquals(expected, parser.parseCommand("  addp   1   -job   Janitor  "));
        assertThrows(ParseException.class, MESSAGE_INVALID_STATUS, () ->
                parser.parseCommand("addp 1 -j Janitor -s ACCEPT"));
    }

    @Test
    public void parseCommand_setApplicationStatus() throws Exception {
        assertEquals(new SetApplicationStatusCommand(INDEX_FIRST_PERSON, Application.Status.ACCEPT),
                parser.parseCommand("set 1 -s accept"));
        assertEquals(new SetApplicationStatusCommand(INDEX_FIRST_PERSON, Application.Status.REJECT),
                parser.parseCommand("  set   1   -status   reject  "));
        assertThrows(ParseException.class, SetApplicationStatusCommand.MESSAGE_INVALID_STATUS, () ->
                parser.parseCommand("set 1 -s ACCEPT"));
        assertThrows(ParseException.class, String.format(SetApplicationStatusCommand.MESSAGE_UNRECOGNIZED,
                "set -p 1 -s accept"), () -> parser.parseCommand("set -p 1 -s accept"));
        assertThrows(ParseException.class, String.format(SetApplicationStatusCommand.MESSAGE_UNRECOGNIZED,
                "set -application 1 -status reject"), () -> parser.parseCommand("set -application 1 -status reject"));
        assertThrows(ParseException.class, MESSAGE_UNKNOWN_COMMAND, () -> parser.parseCommand("Set 1 -s accept"));
    }

    @Test
    public void parseCommand_clear() throws Exception {
        assertTrue(parser.parseCommand(ClearCommand.COMMAND_WORD) instanceof ClearCommand);
        assertTrue(parser.parseCommand(ClearCommand.COMMAND_WORD + " 3") instanceof ClearCommand);
    }

    @Test
    public void parseCommand_jobCommands() throws Exception {
        Job job = new Job("Software Engineer");
        assertEquals(new AddJobCommand(job), parser.parseCommand("addj -j Software Engineer"));
        assertEquals(new AddJobCommand(job), parser.parseCommand("  addj   -job   Software Engineer  "));
        assertEquals(new DeleteJobCommand(INDEX_FIRST_PERSON), parser.parseCommand("deletej 1"));
        assertEquals(new ViewJobCommand(), parser.parseCommand("viewj"));
        assertEquals(new ViewJobCommand("jan"), parser.parseCommand("viewj -f jan"));
        assertThrows(ParseException.class, MESSAGE_UNKNOWN_COMMAND, () -> parser.parseCommand("Addj -j Engineer"));
        assertThrows(ParseException.class, MESSAGE_UNKNOWN_COMMAND, () -> parser.parseCommand("Viewj"));
        assertThrows(ParseException.class, MESSAGE_UNKNOWN_COMMAND, () -> parser.parseCommand("Deletej 1"));
    }

    @Test
    public void parseCommand_delete() throws Exception {
        DeleteCommand command = (DeleteCommand) parser.parseCommand(
                DeleteCommand.COMMAND_WORD + " " + INDEX_FIRST_PERSON.getOneBased());
        assertEquals(new DeleteCommand(INDEX_FIRST_PERSON), command);
    }

    @Test
    public void parseCommand_editCommand_throwsParseException() {
        for (String input : List.of("edit", "edit 1 n/Updated Name", " \tedit 1 p/91234567 \t",
                "edit 1 -n Updated Name", "edit -applicant 1 -n Updated Name", "edit 0")) {
            assertThrows(ParseException.class, MESSAGE_UNKNOWN_COMMAND, () -> parser.parseCommand(input));
        }
    }

    @Test
    public void parseCommand_find() throws Exception {
        List<String> keywords = List.of("foo", "bar", "baz");
        FindCommand command = (FindCommand) parser.parseCommand(
                FindCommand.COMMAND_WORD + " " + keywords.stream().collect(Collectors.joining(" ")));
        assertEquals(new FindCommand(new NameContainsKeywordsPredicate(keywords)), command);
    }

    @Test
    public void parseCommand_help() throws Exception {
        assertTrue(parser.parseCommand(HelpCommand.COMMAND_WORD) instanceof HelpCommand);
        assertTrue(parser.parseCommand(HelpCommand.COMMAND_WORD + " 3") instanceof HelpCommand);
    }

    @Test
    public void parseCommand_list() throws Exception {
        assertTrue(parser.parseCommand(ListCommand.COMMAND_WORD) instanceof ListCommand);
        assertTrue(parser.parseCommand(ListCommand.COMMAND_WORD + " 3") instanceof ListCommand);
    }

    @Test
    public void parseCommand_unrecognisedInput_throwsParseException() {
        assertThrows(ParseException.class, String.format(MESSAGE_INVALID_COMMAND_FORMAT, HelpCommand.MESSAGE_USAGE), ()
            -> parser.parseCommand(""));
    }

    @Test
    public void parseCommand_unknownCommand_throwsParseException() {
        assertThrows(ParseException.class, MESSAGE_UNKNOWN_COMMAND, () -> parser.parseCommand("unknownCommand"));
    }
}
