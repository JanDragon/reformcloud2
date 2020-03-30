package systems.reformcloud.reformcloud2.runner.updater.basic;

import systems.reformcloud.reformcloud2.runner.updater.Updater;
import systems.reformcloud.reformcloud2.runner.util.RunnerUtils;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Properties;

public final class CloudVersionUpdater implements Updater {

    /**
     * Creates a new cloud version updater instance
     *
     * @param globalReformScriptFile The location of the reform script
     */
    public CloudVersionUpdater(@Nonnull File globalReformScriptFile) {
        this.globalReformScriptFile = globalReformScriptFile;
    }

    private final File globalReformScriptFile;

    private boolean versionAvailable = false;

    @Override
    public void collectInformation() {
        String currentVersion = System.getProperty("reformcloud.runner.version");
        if (currentVersion == null) {
            currentVersion = CloudVersionUpdater.class.getPackage().getImplementationVersion();
            this.rewriteGlobalFile(currentVersion);
        }

        Properties properties = RunnerUtils.loadProperties(
                System.getProperty("reformcloud.version.url", "https://internal.reformcloud.systems/version.properties")
        );
        if (properties.containsKey("version")) {
            versionAvailable = !properties.getProperty("version").equals(currentVersion);
        }
    }

    @Override
    public boolean hasNewVersion() {
        return this.versionAvailable;
    }

    @Override
    public void applyUpdates() {
        RunnerUtils.openConnection("https://internal.reformcloud.systems/executor.jar", inputStream -> {
            try {
                Files.copy(inputStream, RunnerUtils.EXECUTOR_PATH, StandardCopyOption.REPLACE_EXISTING);
            } catch (final IOException ex) {
                ex.printStackTrace();
            }
        });

        if (Files.exists(RunnerUtils.RUNNER_FILES_FILE)) {
            System.out.println("Applied update to executor.jar, updating runner.jar...");
            RunnerUtils.openConnection("https://internal.reformcloud.systems/runner.jar", inputStream -> {
                try {
                    Files.copy(inputStream, RunnerUtils.RUNNER_FILES_FILE, StandardCopyOption.REPLACE_EXISTING);
                } catch (final IOException ex) {
                    ex.printStackTrace();
                }
            });
        }
    }

    @Nonnull
    @Override
    public String getName() {
        return "cloud";
    }

    private void rewriteGlobalFile(@Nonnull String currentVersion) {
        System.setProperty("reformcloud.runner.version", currentVersion);

        RunnerUtils.rewriteFile(this.globalReformScriptFile.toPath(), s -> {
            if (s.startsWith("# VARIABLE reformcloud.runner.version=") || s.startsWith("VARIABLE reformcloud.runner.version=")) {
                s = "VARIABLE reformcloud.runner.version=" + currentVersion;
            }

            return s;
        });
    }
}