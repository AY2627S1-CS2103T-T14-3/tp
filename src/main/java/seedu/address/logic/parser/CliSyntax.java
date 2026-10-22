package seedu.address.logic.parser;

/**
 * Contains Command Line Interface (CLI) syntax definitions common to multiple commands
 */
public class CliSyntax {

    /* Option definitions */
    public static final Prefix PREFIX_NAME = new Prefix("-n");
    public static final Prefix PREFIX_PHONE = new Prefix("-p");
    public static final Prefix PREFIX_EMAIL = new Prefix("-e");
    public static final Prefix PREFIX_APPLICANT = new Prefix("-p");
    public static final Prefix PREFIX_JOB = new Prefix("-j");
    public static final Prefix PREFIX_STATUS = new Prefix("-s");

    /* Long aliases for person options */
    public static final Prefix PREFIX_NAME_LONG = new Prefix("-name");
    public static final Prefix PREFIX_PHONE_LONG = new Prefix("-phone");
    public static final Prefix PREFIX_EMAIL_LONG = new Prefix("-email");

}
