package seedu.address.testutil;

import seedu.address.logic.commands.EditCommand.EditPersonDescriptor;
import seedu.address.model.applicant.Applicant;
import seedu.address.model.applicant.Email;
import seedu.address.model.applicant.Name;
import seedu.address.model.applicant.Phone;

/**
 * A utility class to help with building EditPersonDescriptor objects.
 */
public class EditPersonDescriptorBuilder {

    private EditPersonDescriptor descriptor;

    public EditPersonDescriptorBuilder() {
        descriptor = new EditPersonDescriptor();
    }

    public EditPersonDescriptorBuilder(EditPersonDescriptor descriptor) {
        this.descriptor = new EditPersonDescriptor(descriptor);
    }

    /**
     * Returns an {@code EditPersonDescriptor} with fields containing {@code applicant}'s details
     */
    public EditPersonDescriptorBuilder(Applicant applicant) {
        descriptor = new EditPersonDescriptor();
        descriptor.setName(applicant.getName());
        descriptor.setPhone(applicant.getPhone());
        descriptor.setEmail(applicant.getEmail());
    }

    /**
     * Sets the {@code Name} of the {@code EditPersonDescriptor} that we are building.
     */
    public EditPersonDescriptorBuilder withName(String name) {
        descriptor.setName(new Name(name));
        return this;
    }

    /**
     * Sets the {@code Phone} of the {@code EditPersonDescriptor} that we are building.
     */
    public EditPersonDescriptorBuilder withPhone(String phone) {
        descriptor.setPhone(new Phone(phone));
        return this;
    }

    /**
     * Sets the {@code Email} of the {@code EditPersonDescriptor} that we are building.
     */
    public EditPersonDescriptorBuilder withEmail(String email) {
        descriptor.setEmail(new Email(email));
        return this;
    }

    public EditPersonDescriptor build() {
        return descriptor;
    }
}
