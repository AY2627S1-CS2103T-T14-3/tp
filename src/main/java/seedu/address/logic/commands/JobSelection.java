package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.Objects;

import seedu.address.model.application.Job;

/** Identifies a job to select in the jobs view. */
public final class JobSelection implements ViewSelection {
    private final Job job;

    /**
     * Creates a selection for the given job.
     *
     * @param job the job to select.
     */
    public JobSelection(Job job) {
        this.job = requireNonNull(job);
    }

    public Job getJob() {
        return job;
    }

    @Override
    public ViewType getViewType() {
        return ViewType.JOBS;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof JobSelection otherSelection)) {
            return false;
        }
        return job.equals(otherSelection.job);
    }

    @Override
    public int hashCode() {
        return Objects.hash(job);
    }
}
