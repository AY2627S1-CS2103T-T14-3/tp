package seedu.address.model.application;

import static java.util.Objects.requireNonNull;

import java.util.Locale;
import java.util.function.Predicate;

import seedu.address.commons.util.ToStringBuilder;

/**
 * Tests that a {@code Job}'s description contains a keyword.
 * Matching ignores case and does not require a full word, so {@code jan} matches {@code Janitor}.
 */
public class JobDescriptionContainsKeywordPredicate implements Predicate<Job> {
    private final String keyword;

    /**
     * Creates a predicate that matches jobs whose description contains {@code keyword}.
     * @param keyword text to search for, which must not be null
     */
    public JobDescriptionContainsKeywordPredicate(String keyword) {
        this.keyword = requireNonNull(keyword);
    }

    @Override
    public boolean test(Job job) {
        requireNonNull(job);
        return job.toString().toLowerCase(Locale.ROOT).contains(keyword.toLowerCase(Locale.ROOT));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof JobDescriptionContainsKeywordPredicate otherPredicate)) {
            return false;
        }
        return keyword.equals(otherPredicate.keyword);
    }

    @Override
    public int hashCode() {
        return keyword.hashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).add("keyword", keyword).toString();
    }
}
