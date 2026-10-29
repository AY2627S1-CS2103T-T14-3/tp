package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.Objects;

import seedu.address.model.applicant.Applicant;

/** Identifies an applicant to select in the applicants view. */
public final class ApplicantSelection implements ViewSelection {
    private final Applicant applicant;

    /**
     * Creates a selection for the given applicant.
     *
     * @param applicant the applicant to select.
     */
    public ApplicantSelection(Applicant applicant) {
        this.applicant = requireNonNull(applicant);
    }

    public Applicant getApplicant() {
        return applicant;
    }

    @Override
    public ViewType getViewType() {
        return ViewType.APPLICANTS;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof ApplicantSelection otherSelection)) {
            return false;
        }
        return applicant.equals(otherSelection.applicant);
    }

    @Override
    public int hashCode() {
        return Objects.hash(applicant);
    }
}
