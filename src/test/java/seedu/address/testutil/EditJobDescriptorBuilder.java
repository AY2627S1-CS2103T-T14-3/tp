package seedu.address.testutil;

import seedu.address.logic.commands.EditJobCommand.EditJobDescriptor;
import seedu.address.model.application.Job;

/**
 * A utility class to help with building EditJobDescriptor objects.
 */
public class EditJobDescriptorBuilder {

    private EditJobDescriptor descriptor;

    public EditJobDescriptorBuilder() {
        descriptor = new EditJobDescriptor();
    }

    public EditJobDescriptorBuilder(EditJobDescriptor otherDescripter) {
        this.descriptor = new EditJobDescriptor(otherDescripter);
    }

    /**
     * Returns an {@code EditJobDescriptor} with fields containing {@code job} information
     */
    public EditJobDescriptorBuilder(Job job) {
        descriptor = new EditJobDescriptor();
        descriptor.setRole(job.getJobRole());
        descriptor.setDescription(job.getJobDescription());
    }

    /**
     * Sets the {@code Role} of the {@code EditJobDescriptor} that we are building.
     */
    public EditJobDescriptorBuilder withRole(String role) {
        descriptor.setRole(role);
        return this;
    }

    /**
     * Sets the {@code Description} of the {@code EditJobDescriptor} that we are building.
     */
    public EditJobDescriptorBuilder withDescription(String description) {
        descriptor.setDescription(description);
        return this;
    }

    public EditJobDescriptor build() {
        return descriptor;
    }
}
