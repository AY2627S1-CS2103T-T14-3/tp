package seedu.address.commons.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
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
        Path file = directory.resolve("save.json");
        String original = "previous-valid-save";
        Files.writeString(file, original);

        Set<PosixFilePermission> permissions = EnumSet.copyOf(Files.getPosixFilePermissions(directory));
        permissions.remove(PosixFilePermission.OWNER_WRITE);
        permissions.remove(PosixFilePermission.GROUP_WRITE);
        permissions.remove(PosixFilePermission.OTHERS_WRITE);
        Files.setPosixFilePermissions(directory, permissions);
        try {
            assertThrows(IOException.class, () -> FileUtil.writeToFile(file, "replacement-that-must-not-land"));
            assertEquals(original, Files.readString(file));
            assertNoTempFiles(directory);
        } finally {
            permissions.add(PosixFilePermission.OWNER_WRITE);
            Files.setPosixFilePermissions(directory, permissions);
        }
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
