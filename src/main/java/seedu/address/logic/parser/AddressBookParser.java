package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.Messages.MESSAGE_UNKNOWN_COMMAND;

import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import seedu.address.commons.core.LogsCenter;
import seedu.address.logic.commands.AddCommand;
import seedu.address.logic.commands.ByeCommand;
import seedu.address.logic.commands.ClearCommand;
import seedu.address.logic.commands.Command;
import seedu.address.logic.commands.DeleteCommand;
import seedu.address.logic.commands.FindCommand;
import seedu.address.logic.commands.HelpCommand;
import seedu.address.logic.commands.ListCommand;
import seedu.address.logic.commands.SetApplicationStatusCommand;
import seedu.address.logic.commands.ViewCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Parses user input.
 */
public class AddressBookParser {

    public static final String MESSAGE_AMBIGUOUS_ADD = "Unable to tell whether the command %1$s adds an applicant "
            + "or an application.\n"
            + "-p is a phone number for an applicant and an applicant index for an application.\n"
            + "To add an applicant: add -n NAME -p PHONE -e EMAIL\n"
            + "Example: add -n John Doe -p 98765432 -e johnd@example.com\n"
            + "To add an application: add -p INDEX -j JOB_DESCRIPTION [-s STATUS]\n"
            + "Example: add -p 1 -j Janitor -s pending";

    /**
     * Used for initial separation of command word and args.
     */
    private static final Pattern BASIC_COMMAND_FORMAT = Pattern.compile("(?<commandWord>\\S+)(?<arguments>.*)");

    /**
     * Matches the option shared by adding an applicant ({@code -p PHONE}) and adding an
     * application ({@code -p INDEX}). Longer options such as {@code -phone} do not match.
     */
    private static final Pattern SHARED_ADD_OPTION = Pattern.compile("(?<!\\S)-p(?=\\s|$)");

    private static final Logger logger = LogsCenter.getLogger(AddressBookParser.class);

    /**
     * Parses user input into command for execution.
     *
     * @param userInput full user input string
     * @return the command based on the user input
     * @throws ParseException if the user input does not conform to the expected format
     */
    public Command parseCommand(String userInput) throws ParseException {
        final Matcher matcher = BASIC_COMMAND_FORMAT.matcher(userInput.trim());
        if (!matcher.matches()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, HelpCommand.MESSAGE_USAGE));
        }

        final String commandWord = matcher.group("commandWord");
        final String arguments = matcher.group("arguments");

        // Note to developers: Change LOG_LEVEL in LogsCenter to enable lower level (i.e., FINE, FINER and lower)
        // log messages such as the one below.
        // Lower level log messages are used sparingly to minimize noise in the code.
        logger.fine("Command word: " + commandWord + "; Arguments: " + arguments);

        return switch (commandWord) {
            case AddCommand.COMMAND_WORD -> parseAddCommand(arguments);
            case DeleteCommand.COMMAND_WORD -> new DeleteCommandParser().parse(arguments);
            case ClearCommand.COMMAND_WORD -> new ClearCommand();
            case FindCommand.COMMAND_WORD -> new FindCommandParser().parse(arguments);
            case ListCommand.COMMAND_WORD -> new ListCommand();
            case SetApplicationStatusCommand.COMMAND_WORD -> new SetApplicationStatusCommandParser().parse(arguments);
            case ViewCommand.COMMAND_WORD -> new ViewCommandParser().parse(arguments);
            case ByeCommand.COMMAND_WORD, "exit" -> new ByeCommandParser().parse(arguments);
            case HelpCommand.COMMAND_WORD -> new HelpCommand();
            default -> {
                logger.finer("This user input caused a ParseException: " + userInput);
                throw new ParseException(MESSAGE_UNKNOWN_COMMAND);
            }
        };
    }

    /**
     * Parses an add command as either an applicant or an application.
     * An option used by only one of those commands selects that command. A command that uses
     * the shared {@code -p} option without such an option, or that mixes both groups, is rejected
     * as ambiguous so a failed command is not reported as the wrong one.
     * @param arguments arguments string following the add command word
     * @return an AddCommand or AddApplicationCommand object for execution
     * @throws ParseException if the user input does not conform to the expected format
     */
    private Command parseAddCommand(String arguments) throws ParseException {
        boolean addsApplicant = AddCommandParser.hasExclusiveOption(arguments);
        boolean addsApplication = AddApplicationCommandParser.hasExclusiveOption(arguments);
        if (addsApplicant && addsApplication) {
            throw ambiguousAddCommand(arguments);
        }
        if (!addsApplicant && !addsApplication && SHARED_ADD_OPTION.matcher(arguments).find()) {
            throw ambiguousAddCommand(arguments);
        }
        if (addsApplication) {
            return new AddApplicationCommandParser().parse(arguments);
        }
        return new AddCommandParser().parse(arguments);
    }

    /**
     * Returns a parse exception that shows both add formats.
     * @param arguments arguments string following the add command word
     * @return parse exception describing the two add commands
     */
    private static ParseException ambiguousAddCommand(String arguments) {
        String input = (AddCommand.COMMAND_WORD + arguments).trim().replaceAll("\\s+", " ");
        return new ParseException(String.format(MESSAGE_AMBIGUOUS_ADD, input));
    }

}
