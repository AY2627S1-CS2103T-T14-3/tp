package seedu.address.logic.parser;

/**
 * Contains Command Line Interface (CLI) syntax definitions common to multiple commands
 */
public class CliSyntax {

    /* Option definitions */
    public static final Option OPTION_NAME_REQ = Option.required("-n", "-name");
    public static final Option OPTION_PHONE_REQ = Option.required("-p", "-phone");
    public static final Option OPTION_EMAIL_REQ = Option.required("-e", "-email");
    public static final Option OPTION_STATUS_REQ = Option.required("-s", "-status");
    public static final Option OPTION_JOB_REQ = Option.required("-j", "-job");

    public static final Option OPTION_NAME_OPT = Option.optional("-n", "-name");
    public static final Option OPTION_PHONE_OPT = Option.optional("-p", "-phone");
    public static final Option OPTION_EMAIL_OPT = Option.optional("-e", "-email");
    public static final Option OPTION_STATUS_OPT = Option.optional("-s", "-status");
    public static final Option OPTION_JOB_OPT = Option.optional("-j", "-job");

    public static final Option OPTION_EMPTY = Option.OPTION_EMPTY;

    public static final Option OPTION_FILTER = Option.optional("-f", "-filter");
}
