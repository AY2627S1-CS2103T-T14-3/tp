package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.Optional;

import seedu.address.model.applicant.Applicant;
import seedu.address.model.application.Application;
import seedu.address.model.application.Job;

/** Describes a view to display and an optional item to select in that view. */
public final class ViewRequest {
    private final ViewType viewType;
    private final Optional<ViewSelection> selection;

    /**
     * Creates a request for a view and an optional item to select in that view.
     *
     * @param viewType the view to display.
     * @param selection the optional item to select.
     */
    public ViewRequest(ViewType viewType, Optional<ViewSelection> selection) {
        this.viewType = requireNonNull(viewType);
        this.selection = requireNonNull(selection);

        selection.ifPresent(selected -> {
            if (selected.getViewType() != viewType) {
                throw new IllegalArgumentException(
                        "Selection does not belong to the requested view");
            }
        });
    }

    /** Returns the view to display. */
    public ViewType getViewType() {
        return viewType;
    }

    /** Returns the optional item to select in the view. */
    public Optional<ViewSelection> getSelection() {
        return selection;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof ViewRequest otherRequest)) {
            return false;
        }
        return viewType == otherRequest.viewType && selection.equals(otherRequest.selection);
    }

    @Override
    public int hashCode() {
        return 31 * viewType.hashCode() + selection.hashCode();
    }

    /** Creates a request to display the applicants view without a selection. */
    public static ViewRequest applicants() {
        return new ViewRequest(ViewType.APPLICANTS, Optional.empty());
    }

    /**
     * Creates a request to display the applicants view and select an applicant.
     *
     * @param applicant the applicant to select.
     * @return a request for the applicants view
     */
    public static ViewRequest applicant(Applicant applicant) {
        return new ViewRequest(ViewType.APPLICANTS,
                Optional.of(new ApplicantSelection(applicant)));
    }

    /** Creates a request to display the applications view without a selection. */
    public static ViewRequest applications() {
        return new ViewRequest(ViewType.APPLICATIONS, Optional.empty());
    }

    /**
     * Creates a request to display the applications view and select an application.
     *
     * @param application the application to select.
     * @return a request for the applications view
     */
    public static ViewRequest application(Application application) {
        return new ViewRequest(ViewType.APPLICATIONS,
                Optional.of(new ApplicationSelection(application)));
    }

    /** Creates a request to display the jobs view without a selection. */
    public static ViewRequest jobs() {
        return new ViewRequest(ViewType.JOBS, Optional.empty());
    }

    /**
     * Creates a request to display the jobs view and select a job.
     *
     * @param job the job to select.
     * @return a request for the jobs view
     */
    public static ViewRequest job(Job job) {
        return new ViewRequest(ViewType.JOBS,
                Optional.of(new JobSelection(job)));
    }
}
