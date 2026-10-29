package seedu.address.testutil;

import static seedu.address.logic.commands.CommandTestUtil.VALID_EMAIL_AMY;
import static seedu.address.logic.commands.CommandTestUtil.VALID_EMAIL_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_NAME_AMY;
import static seedu.address.logic.commands.CommandTestUtil.VALID_NAME_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_PHONE_AMY;
import static seedu.address.logic.commands.CommandTestUtil.VALID_PHONE_BOB;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import seedu.address.model.AddressBook;
import seedu.address.model.applicant.Applicant;

/**
 * A utility class containing a list of {@code Applicant} objects to be used in tests.
 */
public class TypicalPersons {

    public static final Applicant ALICE = new ApplicantBuilder().withName("Alice Pauline")
            .withPhone("94351253").withEmail("alice@example.com").build();
    public static final Applicant BENSON = new ApplicantBuilder().withName("Benson Meier")
            .withEmail("johnd@example.com").withPhone("98765432").build();
    public static final Applicant CARL = new ApplicantBuilder().withName("Carl Kurz").withPhone("95352563")
            .withEmail("heinz@example.com").build();
    public static final Applicant DANIEL = new ApplicantBuilder().withName("Daniel Meier").withPhone("87652533")
            .withEmail("cornelia@example.com").build();
    public static final Applicant ELLE = new ApplicantBuilder().withName("Elle Meyer").withPhone("9482224")
            .withEmail("werner@example.com").build();
    public static final Applicant FIONA = new ApplicantBuilder().withName("Fiona Kunz").withPhone("9482427")
            .withEmail("lydia@example.com").build();
    public static final Applicant GEORGE = new ApplicantBuilder().withName("George Best").withPhone("9482442")
            .withEmail("anna@example.com").build();

    // Manually added
    public static final Applicant HOON = new ApplicantBuilder().withName("Hoon Meier").withPhone("8482424")
            .withEmail("stefan@example.com").build();
    public static final Applicant IDA = new ApplicantBuilder().withName("Ida Mueller").withPhone("8482131")
            .withEmail("hans@example.com").build();

    // Manually added - Applicant's details found in {@code CommandTestUtil}
    public static final Applicant AMY = new ApplicantBuilder().withName(VALID_NAME_AMY).withPhone(VALID_PHONE_AMY)
            .withEmail(VALID_EMAIL_AMY).build();
    public static final Applicant BOB = new ApplicantBuilder().withName(VALID_NAME_BOB).withPhone(VALID_PHONE_BOB)
            .withEmail(VALID_EMAIL_BOB).build();

    public static final String KEYWORD_MATCHING_MEIER = "Meier"; // A keyword that matches MEIER

    private TypicalPersons() {} // prevents instantiation

    /**
     * Returns an {@code AddressBook} with all the typical persons.
     */
    public static AddressBook getTypicalAddressBook() {
        AddressBook ab = new AddressBook();
        for (Applicant applicant : getTypicalPersons()) {
            ab.addPerson(applicant);
        }
        return ab;
    }

    public static List<Applicant> getTypicalPersons() {
        return new ArrayList<>(Arrays.asList(ALICE, BENSON, CARL, DANIEL, ELLE, FIONA, GEORGE));
    }
}
