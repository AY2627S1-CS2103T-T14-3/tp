package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;
import static seedu.address.model.Model.PREDICATE_SHOW_ALL_JOBS;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.CollectionUtil;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.application.Job;

/**
 * Replaces the role of a job identified using its displayed index and updates its applications.
 */
public class EditJobCommand extends Command {

    public static final String COMMAND_WORD = "editj";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Edits the job identified by the index number used in the displayed job list.\n"
            + "Parameters: INDEX (must be a positive integer) -j JOB_ROLE\n"
            + "Alias: -job for -j\n"
            + "Example: " + COMMAND_WORD + " 1 -j Software Engineer Intern";

    public static final String MESSAGE_EDIT_JOB_SUCCESS = "Edited job: %1$s";
    public static final String MESSAGE_DUPLICATE_JOB = "This job already exists in the job list.";
    public static final String MESSAGE_NOT_EDITED = "At least one field to edit must be provided.";
    public static final String MESSAGE_EMPTY_LIST = "There are no jobs to edit.";
    public static final String MESSAGE_INVALID_INDEX =
            "Invalid job index. Please enter an integer from 1 to %1$d.";

    private final Index targetIndex;
    private final EditJobDescriptor editJobDescriptor;

    /**
     * Creates a command to replace the job at {@code targetIndex} with {@code editedJob}.
     */
    public EditJobCommand(Index targetIndex, EditJobDescriptor editedJob) {
        requireAllNonNull(targetIndex, editedJob);
        this.targetIndex = targetIndex;
        this.editJobDescriptor = editedJob;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        List<Job> lastShownList = model.getFilteredJobList();

        if (lastShownList.isEmpty()) {
            throw new CommandException(MESSAGE_EMPTY_LIST);
        }
        if (targetIndex.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(String.format(MESSAGE_INVALID_INDEX, lastShownList.size()));
        }

        Job jobToEdit = lastShownList.get(targetIndex.getZeroBased());
        Job editedJob = createEditedPerson(jobToEdit, editJobDescriptor);

        if (!jobToEdit.equals(editedJob) && model.hasJob(editedJob)) {
            throw new CommandException(MESSAGE_DUPLICATE_JOB);
        }

        model.setJob(jobToEdit, editedJob);
        model.updateFilteredJobList(PREDICATE_SHOW_ALL_JOBS);
        return new CommandResult(String.format(MESSAGE_EDIT_JOB_SUCCESS, Messages.format(editedJob)),
                ViewRequest.job(editedJob));
    }

    private static Job createEditedPerson(Job jobToEdit, EditJobDescriptor editJobDescriptor) {
        assert jobToEdit != null;

        String role = editJobDescriptor.getRole().orElse(jobToEdit.getJobRole());
        String description = editJobDescriptor.getDescription().orElse(jobToEdit.getJobDescription());

        return new Job(role, description);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof EditJobCommand otherCommand)) {
            return false;
        }
        return targetIndex.equals(otherCommand.targetIndex) && editJobDescriptor.equals(otherCommand.editJobDescriptor);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("targetIndex", targetIndex)
                .add("editedJob", editJobDescriptor)
                .toString();
    }


    /**
     * Stores the details to edit the person with. Each non-empty field value will replace the
     * corresponding field value of the person.
     */
    public static class EditJobDescriptor {
        private String jobRole;
        private String jobDescription;

        public EditJobDescriptor() {}

        /**
         * Copy constructor.
         */
        public EditJobDescriptor(EditJobDescriptor toCopy) {
            setRole(toCopy.jobRole);
            setDescription(toCopy.jobDescription);
        }

        /**
         * Returns true if at least one field is edited.
         */
        public boolean isAnyFieldEdited() {
            return CollectionUtil.isAnyNonNull(jobRole, jobDescription);
        }

        public void setRole(String jobRole) {
            this.jobRole = jobRole;
        }

        public Optional<String> getRole() {
            return Optional.ofNullable(jobRole);
        }

        public void setDescription(String jobDescription) {
            this.jobDescription = jobDescription;
        }

        public Optional<String> getDescription() {
            return Optional.ofNullable(jobDescription);
        }

        @Override
        public boolean equals(Object other) {
            if (other == this) {
                return true;
            }

            // instanceof handles nulls
            if (!(other instanceof EditJobDescriptor otherEditJobDescriptor)) {
                return false;
            }

            return Objects.equals(jobRole, otherEditJobDescriptor.jobRole)
                    && Objects.equals(jobDescription, otherEditJobDescriptor.jobDescription);
        }

        @Override
        public String toString() {
            return new ToStringBuilder(this)
                    .add("jobRole", jobRole)
                    .add("jobDescription", jobDescription)
                    .toString();
        }
    }
}
