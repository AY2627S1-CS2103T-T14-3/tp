package seedu.address.commons.util;

import static java.util.Objects.requireNonNull;

import java.io.IOException;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

/**
 * Writes and reads files
 */
public class FileUtil {

    private static final String CHARSET = "UTF-8";

    /**
     * Creates a file if it does not exist along with its missing parent directories.
     * @throws IOException if the file or directory cannot be created.
     */
    public static void createIfMissing(Path file) throws IOException {
        if (Files.exists(file)) {
            return;
        }

        createParentDirsOfFile(file);

        Files.createFile(file);
    }

    /**
     * Creates parent directories of file if it has a parent directory
     */
    private static void createParentDirsOfFile(Path file) throws IOException {
        Path parentDir = file.getParent();

        if (parentDir != null) {
            Files.createDirectories(parentDir);
        }
    }

    /**
     * Assumes file exists
     */
    public static String readFromFile(Path file) throws IOException {
        return new String(Files.readAllBytes(file), CHARSET);
    }

    /**
     * Writes {@code content} to {@code file}, creating the file if it does not exist.
     * An existing file is replaced only after the new content has been written fully,
     * so a failed write leaves the previous file unchanged.
     *
     * @param file Destination file. Cannot be null. Its parent directory must exist.
     * @param content Text to write. Cannot be null.
     * @throws IOException If the file cannot be written.
     */
    public static void writeToFile(Path file, String content) throws IOException {
        requireNonNull(file);
        requireNonNull(content);

        Path destination = file.toAbsolutePath();
        // Follow a symbolic link so the save updates its target, not the link itself.
        if (Files.isSymbolicLink(destination)) {
            destination = destination.toRealPath();
        }
        Path parentDir = destination.getParent();
        if (parentDir == null) {
            throw new IOException("File has no parent directory: " + file);
        }

        // A temporary file in the same directory can replace the save atomically.
        Path tempFile = Files.createTempFile(parentDir, "linkedout", ".tmp");
        try {
            Files.write(tempFile, content.getBytes(CHARSET));
            moveIntoPlace(tempFile, destination);
        } catch (IOException writeError) {
            try {
                Files.deleteIfExists(tempFile);
            } catch (IOException deleteError) {
                writeError.addSuppressed(deleteError);
            }
            throw writeError;
        }
    }

    /**
     * Replaces {@code destination} with {@code tempFile}.
     * Uses an atomic move when the file system supports it.
     */
    private static void moveIntoPlace(Path tempFile, Path destination) throws IOException {
        try {
            Files.move(tempFile, destination, StandardCopyOption.REPLACE_EXISTING,
                    StandardCopyOption.ATOMIC_MOVE);
        } catch (AtomicMoveNotSupportedException e) {
            Files.move(tempFile, destination, StandardCopyOption.REPLACE_EXISTING);
        }
    }

}
