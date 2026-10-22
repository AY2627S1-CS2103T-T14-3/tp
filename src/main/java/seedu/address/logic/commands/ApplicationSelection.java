package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.Objects;

import seedu.address.model.application.Application;

/** Identifies an application to select in the applications view. */
public final class ApplicationSelection implements ViewSelection {
    private final Application application;

    public ApplicationSelection(Application application) {
        this.application = requireNonNull(application);
    }

    public Application getApplication() {
        return application;
    }

    @Override
    public ViewType getViewType() {
        return ViewType.APPLICATIONS;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof ApplicationSelection otherSelection)) {
            return false;
        }
        return application.equals(otherSelection.application);
    }

    @Override
    public int hashCode() {
        return Objects.hash(application);
    }
}
