package seedu.address.commons.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Tests that a logging setup failure does not prevent startup.
 */
public class LogsCenterTest {

    @TempDir
    public Path tempDir;

    @Test
    public void classInit_logFileBlockedByDirectory_exitsCleanly() throws Exception {
        // Matches LogsCenter's rolling log file, addressbook.log.0.
        Files.createDirectory(tempDir.resolve("addressbook.log.0"));
        String javaBinary = Path.of(System.getProperty("java.home"), "bin", "java").toString();
        ProcessBuilder builder = new ProcessBuilder(
                javaBinary,
                "-cp",
                System.getProperty("java.class.path"),
                LogsCenterTest.class.getName());
        builder.directory(tempDir.toFile());
        builder.redirectErrorStream(true);

        Process process = builder.start();
        boolean finished = process.waitFor(30, TimeUnit.SECONDS);
        String output = new String(process.getInputStream().readAllBytes());
        if (!finished) {
            process.destroyForcibly();
        }

        assertTrue(finished, output);
        assertEquals(0, process.exitValue(), output);
        assertTrue(output.contains("Error adding file handler for logger."), output);
    }

    /**
     * Starts logging in a fresh process so a blocked log file can be tested.
     */
    public static void main(String[] args) {
        LogsCenter.getLogger(LogsCenterTest.class).info("logging ready");
    }
}
