package seedu.address.commons.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeFalse;
import static org.junit.jupiter.api.Assumptions.assumeTrue;
import static seedu.address.testutil.Assert.assertThrows;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFilePermission;
import java.util.EnumSet;
import java.util.Set;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Tests file writing, including a failed write that must keep the previous file.
 */
public class FileUtilTest {

    @TempDir
    public Path tempDir;

    @Test
    public void writeToFile_newAndExistingFile_writesCompleteContent() throws Exception {
        Path file = tempDir.resolve("save.json");

        FileUtil.writeToFile(file, "first");
        assertEquals("first", Files.readString(file));
        assertNoTempFiles(tempDir);

        FileUtil.writeToFile(file, "second");
        assertEquals("second", Files.readString(file));
        assertNoTempFiles(tempDir);
    }

    @Test
    public void writeToFile_parentNotWritable_keepsExistingContent() throws Exception {
        Path directory = tempDir.resolve("locked");
        Files.createDirectory(directory);
        assumeTrue(directory.getFileSystem().supportedFileAttributeViews().contains("posix"),
                "POSIX permissions are not supported");

        Path file = directory.resolve("save.json");
        String original = "previous-valid-save";
        Files.writeString(file, original);

        Set<PosixFilePermission> permissions = EnumSet.copyOf(Files.getPosixFilePermissions(directory));
        permissions.remove(PosixFilePermission.OWNER_WRITE);
        permissions.remove(PosixFilePermission.GROUP_WRITE);
        permissions.remove(PosixFilePermission.OTHERS_WRITE);
        Files.setPosixFilePermissions(directory, permissions);
        try {
            assumeFalse(Files.isWritable(directory),
                    "Directory is still writable after removing write permission");
            assertThrows(IOException.class, () -> FileUtil.writeToFile(file, "replacement-that-must-not-land"));
            assertEquals(original, Files.readString(file));
            assertNoTempFiles(directory);
        } finally {
            permissions.add(PosixFilePermission.OWNER_WRITE);
            Files.setPosixFilePermissions(directory, permissions);
        }
    }

    @Test
    public void writeToFile_symbolicLink_updatesTargetAndKeepsLink() throws Exception {
        Path targetDirectory = tempDir.resolve("elsewhere");
        Files.createDirectory(targetDirectory);
        Path target = targetDirectory.resolve("real-save.json");
        Files.writeString(target, "previous");
        Path link = tempDir.resolve("addressbook.json");
        try {
            Files.createSymbolicLink(link, target);
        } catch (UnsupportedOperationException | IOException e) {
            assumeTrue(false, "Symbolic links are not supported");
        }

        FileUtil.writeToFile(link, "updated");

        assertTrue(Files.isSymbolicLink(link));
        assertEquals("updated", Files.readString(target));
        assertEquals("updated", Files.readString(link));
        assertNoTempFiles(targetDirectory);
    }

    @Test
    public void writeToFile_nullInputs_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> FileUtil.writeToFile(null, "content"));
        assertThrows(NullPointerException.class, () -> FileUtil.writeToFile(tempDir.resolve("save.json"), null));
    }

    private void assertNoTempFiles(Path directory) throws IOException {
        try (Stream<Path> paths = Files.list(directory)) {
            assertTrue(paths.noneMatch(path -> path.getFileName().toString().endsWith(".tmp")));
        }
    }
}
