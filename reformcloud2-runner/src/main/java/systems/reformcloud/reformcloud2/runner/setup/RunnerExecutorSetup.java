package systems.reformcloud.reformcloud2.runner.setup;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.runner.util.RunnerUtils;

import java.io.Console;
import java.util.function.Consumer;
import java.util.function.Predicate;

public final class RunnerExecutorSetup {

    private RunnerExecutorSetup() {
        throw new UnsupportedOperationException();
    }

    /**
     * Executes the runner executor setup
     */
    public static void executeSetup() {
        System.out.println("Please choose an executor: \"node\" (recommended), \"controller\", \"client\"");
        System.out.println("For more information check out the README on GitHub: " + RunnerUtils.REPO_BASE_URL);

        String executor = readFromConsoleOrFromSystemProperties(
                s -> RunnerUtils.AVAILABLE_EXECUTORS.contains(s.toLowerCase()),
                s -> {
                    System.out.println("The executor " + s + " is not available.");
                    System.out.println("Please choose one of these: " + String.join(", ", RunnerUtils.AVAILABLE_EXECUTORS));
                });
        System.setProperty(
                "reformcloud.executor.type",
                Integer.toString(getIDFromType(executor))
        );
    }

    @NotNull
    private static String readFromConsoleOrFromSystemProperties(@NotNull Predicate<String> predicate,
                                                                @NotNull Consumer<String> wrongInput) {
        String property = System.getProperty("reformcloud.executor.type");
        if (property != null && predicate.test(property)) {
            return property;
        }

        Console console = System.console();
        String s = console.readLine();
        while (s == null || s.trim().isEmpty() || !predicate.test(s)) {
            wrongInput.accept(s);
            s = console.readLine();
        }

        return s;
    }

    private static int getIDFromType(@NotNull String type) {
        if (type.equalsIgnoreCase("node")) {
            return 4;
        }

        return type.equalsIgnoreCase("controller") ? 1 : 2;
    }

}
