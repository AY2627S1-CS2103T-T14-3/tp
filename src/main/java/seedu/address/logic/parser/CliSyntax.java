package seedu.address.logic.parser;

/**
 * Contains Command Line Interface (CLI) syntax definitions common to multiple commands
 */
public class CliSyntax {

    /* Option definitions */
    public static final Option OPTION_NAME = Option.required("-n", "-name");
    public static final Option OPTION_PHONE = Option.required("-p", "-phone");
    public static final Option OPTION_EMAIL = Option.required("-e", "-email");
    public static final Option OPTION_APPLICANT = Option.required("-app", "-application");
    public static final Option OPTION_JOB = Option.required("-j", "-job");
    public static final Option OPTION_STATUS = Option.optional("-s", "-status");

    public static final Option OPTION_EMPTY = Option.required("");

}
