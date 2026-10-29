package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.OPTION_EMAIL_REQ;
import static seedu.address.logic.parser.CliSyntax.OPTION_NAME_REQ;
import static seedu.address.logic.parser.CliSyntax.OPTION_PHONE_REQ;
import static seedu.address.model.Model.PREDICATE_SHOW_ALL_PERSONS;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.CollectionUtil;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.applicant.Applicant;
import seedu.address.model.applicant.Email;
import seedu.address.model.applicant.Name;
import seedu.address.model.applicant.Phone;

/**
 * Edits the details of an existing applicant in the address book.
 */
public class EditCommand extends Command {

    public static final String COMMAND_WORD = "edit";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Edits the details of the applicant identified "
            + "by the index number used in the displayed applicant list. "
            + "Existing values will be overwritten by the input values.\n"
            + "Parameters: INDEX (must be a positive integer) "
            + "[" + OPTION_NAME_REQ + "NAME] "
            + "[" + OPTION_PHONE_REQ + "PHONE] "
            + "[" + OPTION_EMAIL_REQ + "EMAIL]...\n"
            + "Example: " + COMMAND_WORD + " 1 "
            + OPTION_PHONE_REQ + "91234567 "
            + OPTION_EMAIL_REQ + "johndoe@example.com";

    public static final String MESSAGE_EDIT_PERSON_SUCCESS = "Edited applicant: %1$s";
    public static final String MESSAGE_NOT_EDITED = "At least one field to edit must be provided.";
    public static final String MESSAGE_DUPLICATE_PERSON = "An applicant with this name or email already exists.";

    private final Index index;
    private final EditPersonDescriptor editPersonDescriptor;

    /**
     * @param index of the applicant in the filtered applicant list to edit
     * @param editPersonDescriptor details to edit the applicant with
     */
    public EditCommand(Index index, EditPersonDescriptor editPersonDescriptor) {
        requireNonNull(index);
        requireNonNull(editPersonDescriptor);

        this.index = index;
        this.editPersonDescriptor = new EditPersonDescriptor(editPersonDescriptor);
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        List<Applicant> lastShownList = model.getFilteredPersonList();

        if (index.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        }

        Applicant applicantToEdit = lastShownList.get(index.getZeroBased());
        Applicant editedApplicant = createEditedPerson(applicantToEdit, editPersonDescriptor);

        boolean hasDuplicate = model.getAddressBook().getPersonList().stream()
                .filter(person -> !person.equals(applicantToEdit))
                .anyMatch(editedApplicant::hasSameNameOrEmail);
        if (hasDuplicate) {
            throw new CommandException(MESSAGE_DUPLICATE_PERSON);
        }

        model.setPerson(applicantToEdit, editedApplicant);
        model.updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);
        return new CommandResult(String.format(MESSAGE_EDIT_PERSON_SUCCESS, Messages.format(editedApplicant)));
    }

    /**
     * Creates and returns a {@code Applicant} with the details of {@code applicantToEdit}
     * edited with {@code editPersonDescriptor}.
     */
    private static Applicant createEditedPerson(Applicant applicantToEdit, EditPersonDescriptor editPersonDescriptor) {
        assert applicantToEdit != null;

        Name updatedName = editPersonDescriptor.getName().orElse(applicantToEdit.getName());
        Phone updatedPhone = editPersonDescriptor.getPhone().orElse(applicantToEdit.getPhone());
        Email updatedEmail = editPersonDescriptor.getEmail().orElse(applicantToEdit.getEmail());

        return new Applicant(updatedName, updatedPhone, updatedEmail);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof EditCommand otherEditCommand)) {
            return false;
        }

        return index.equals(otherEditCommand.index)
                && editPersonDescriptor.equals(otherEditCommand.editPersonDescriptor);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("index", index)
                .add("editPersonDescriptor", editPersonDescriptor)
                .toString();
    }

    /**
     * Stores the details to edit the applicant with. Each non-empty field value will replace the
     * corresponding field value of the applicant.
     */
    public static class EditPersonDescriptor {
        private Name name;
        private Phone phone;
        private Email email;

        public EditPersonDescriptor() {}

        /**
         * Copy constructor.
         */
        public EditPersonDescriptor(EditPersonDescriptor toCopy) {
            setName(toCopy.name);
            setPhone(toCopy.phone);
            setEmail(toCopy.email);
        }

        /**
         * Returns true if at least one field is edited.
         */
        public boolean isAnyFieldEdited() {
            return CollectionUtil.isAnyNonNull(name, phone, email);
        }

        public void setName(Name name) {
            this.name = name;
        }

        public Optional<Name> getName() {
            return Optional.ofNullable(name);
        }

        public void setPhone(Phone phone) {
            this.phone = phone;
        }

        public Optional<Phone> getPhone() {
            return Optional.ofNullable(phone);
        }

        public void setEmail(Email email) {
            this.email = email;
        }

        public Optional<Email> getEmail() {
            return Optional.ofNullable(email);
        }

        @Override
        public boolean equals(Object other) {
            if (other == this) {
                return true;
            }

            // instanceof handles nulls
            if (!(other instanceof EditPersonDescriptor otherEditPersonDescriptor)) {
                return false;
            }

            return Objects.equals(name, otherEditPersonDescriptor.name)
                    && Objects.equals(phone, otherEditPersonDescriptor.phone)
                    && Objects.equals(email, otherEditPersonDescriptor.email);
        }

        @Override
        public String toString() {
            return new ToStringBuilder(this)
                    .add("name", name)
                    .add("phone", phone)
                    .add("email", email)
                    .toString();
        }
    }
}
