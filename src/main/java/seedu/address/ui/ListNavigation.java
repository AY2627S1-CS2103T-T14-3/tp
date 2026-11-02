package seedu.address.ui;

import static java.util.Objects.requireNonNull;

import javafx.scene.control.ListView;

/**
 * Moves the selection within a displayed list.
 */
final class ListNavigation {

    private ListNavigation() {}

    /**
     * Selects the item {@code offset} rows from the current selection and scrolls it into view.
     * A missing selection moves to the first item for a positive offset and the last item for a
     * negative offset. The selection stops at either end, and an empty list is left unchanged.
     *
     * @param listView list whose selection changes. Cannot be null.
     * @param offset number of rows to move. Positive moves down.
     */
    static void selectOffset(ListView<?> listView, int offset) {
        requireNonNull(listView);
        int size = listView.getItems().size();
        if (size == 0 || offset == 0) {
            return;
        }

        int currentIndex = listView.getSelectionModel().getSelectedIndex();
        int nextIndex = nextIndex(currentIndex, offset, size);
        if (nextIndex == currentIndex) {
            return;
        }

        listView.getSelectionModel().select(nextIndex);
        listView.scrollTo(nextIndex);
    }

    private static int nextIndex(int currentIndex, int offset, int size) {
        if (currentIndex < 0) {
            return offset > 0 ? 0 : size - 1;
        }

        int nextIndex = currentIndex + offset;
        if (nextIndex < 0) {
            return 0;
        }
        if (nextIndex >= size) {
            return size - 1;
        }
        return nextIndex;
    }
}
