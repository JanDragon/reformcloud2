package systems.reformcloud.reformcloud2.executor.api.common.api.process;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import systems.reformcloud.reformcloud2.executor.api.api.API;
import systems.reformcloud.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.process.ProcessInformation;
import systems.reformcloud.reformcloud2.executor.api.common.process.api.ProcessConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.process.api.ProcessConfigurationBuilder;
import systems.reformcloud.reformcloud2.executor.api.common.process.api.ProcessInclusion;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

public interface ProcessSyncAPI {

    /**
     * Starts a process
     *
     * @param groupName The name of the group which should be started from
     * @return The created {@link ProcessInformation}
     */
    @Nullable
    default ProcessInformation startProcess(@NotNull String groupName) {
        return this.startProcess(groupName, null);
    }

    /**
     * Starts a process
     *
     * @param groupName The name of the group which should be started from
     * @param template  The template which should be used
     * @return The created {@link ProcessInformation}
     */
    @Nullable
    default ProcessInformation startProcess(@NotNull String groupName, @Nullable String template) {
        return this.startProcess(groupName, template, new JsonConfiguration());
    }

    /**
     * Starts a process
     *
     * @param groupName    The name of the group which should be started from
     * @param template     The template which should be used
     * @param configurable The data for the process
     * @return The created {@link ProcessInformation}
     */
    @Nullable
    default ProcessInformation startProcess(@NotNull String groupName, @Nullable String template, @NotNull JsonConfiguration configurable) {
        return this.startProcess(groupName, template, configurable, UUID.randomUUID());
    }

    /**
     * Starts a process
     *
     * @param groupName    The name of the group which should be started from
     * @param template     The template which should be used
     * @param configurable The data for the process
     * @param uniqueID     The unique id which should get used for the process
     * @return The created {@link ProcessInformation}
     */
    @Nullable
    default ProcessInformation startProcess(@NotNull String groupName, @Nullable String template,
                                            @NotNull JsonConfiguration configurable, @NotNull UUID uniqueID) {
        return this.startProcess(groupName, template, configurable, uniqueID, null);
    }

    /**
     * Starts a process
     *
     * @param groupName    The name of the group which should be started from
     * @param template     The template which should be used
     * @param configurable The data for the process
     * @param uniqueID     The unique id which should get used for the process
     * @param displayName  The display name of the new process
     * @return The created {@link ProcessInformation}
     */
    @Nullable
    default ProcessInformation startProcess(@NotNull String groupName, @Nullable String template,
                                            @NotNull JsonConfiguration configurable, @NotNull UUID uniqueID,
                                            @Nullable String displayName) {
        return this.startProcess(groupName, template, configurable, uniqueID, displayName, null);
    }

    /**
     * Starts a process
     *
     * @param groupName    The name of the group which should be started from
     * @param template     The template which should be used
     * @param configurable The data for the process
     * @param uniqueID     The unique id which should get used for the process
     * @param displayName  The display name of the new process
     * @param maxMemory    The maximum amount of memory which the new process is allowed to use
     * @return The created {@link ProcessInformation}
     */
    @Nullable
    default ProcessInformation startProcess(@NotNull String groupName, @Nullable String template,
                                            @NotNull JsonConfiguration configurable, @NotNull UUID uniqueID,
                                            @Nullable String displayName, @Nullable Integer maxMemory) {
        return this.startProcess(groupName, template, configurable, uniqueID, displayName, maxMemory, null);
    }

    /**
     * Starts a process
     *
     * @param groupName    The name of the group which should be started from
     * @param template     The template which should be used
     * @param configurable The data for the process
     * @param uniqueID     The unique id which should get used for the process
     * @param displayName  The display name of the new process
     * @param maxMemory    The maximum amount of memory which the new process is allowed to use
     * @param port         The port of the new process (If it's already in use a random port will be used)
     * @return The created {@link ProcessInformation}
     */
    @Nullable
    default ProcessInformation startProcess(@NotNull String groupName, @Nullable String template,
                                            @NotNull JsonConfiguration configurable, @NotNull UUID uniqueID,
                                            @Nullable String displayName, @Nullable Integer maxMemory,
                                            @Nullable Integer port) {
        return this.startProcess(groupName, template, configurable, uniqueID, displayName, maxMemory, port, null);
    }

    /**
     * Starts a process
     *
     * @param groupName    The name of the group which should be started from
     * @param template     The template which should be used
     * @param configurable The data for the process
     * @param uniqueID     The unique id which should get used for the process
     * @param displayName  The display name of the new process
     * @param maxMemory    The maximum amount of memory which the new process is allowed to use
     * @param port         The port of the new process (If it's already in use a random port will be used)
     * @param id           The id of the process (for example {@code 1}). The name of the process will also use
     *                     the given id (for {@code 1} it might be {@code Lobby-1}). If the id is already taken
     *                     a random id will get used
     * @return The created {@link ProcessInformation}
     */
    @Nullable
    default ProcessInformation startProcess(@NotNull String groupName, @Nullable String template,
                                            @NotNull JsonConfiguration configurable, @NotNull UUID uniqueID,
                                            @Nullable String displayName, @Nullable Integer maxMemory,
                                            @Nullable Integer port, @Nullable Integer id) {
        return this.startProcess(groupName, template, configurable, uniqueID, displayName, maxMemory, port, id, null);
    }

    /**
     * Starts a process
     *
     * @param groupName    The name of the group which should be started from
     * @param template     The template which should be used
     * @param configurable The data for the process
     * @param uniqueID     The unique id which should get used for the process
     * @param displayName  The display name of the new process
     * @param maxMemory    The maximum amount of memory which the new process is allowed to use
     * @param port         The port of the new process (If it's already in use a random port will be used)
     * @param id           The id of the process (for example {@code 1}). The name of the process will also use
     *                     the given id (for {@code 1} it might be {@code Lobby-1}). If the id is already taken
     *                     a random id will get used
     * @param maxPlayers   The maximum amount of player which are allowed to join the process
     * @return The created {@link ProcessInformation}
     */
    @Nullable
    default ProcessInformation startProcess(@NotNull String groupName, @Nullable String template,
                                            @NotNull JsonConfiguration configurable, @NotNull UUID uniqueID,
                                            @Nullable String displayName, @Nullable Integer maxMemory,
                                            @Nullable Integer port, @Nullable Integer id, @Nullable Integer maxPlayers) {
        return this.startProcess(groupName, template, configurable, uniqueID, displayName, maxMemory, port, id, maxPlayers, new ArrayList<>());
    }

    /**
     * Starts a process
     *
     * @param groupName    The name of the group which should be started from
     * @param template     The template which should be used
     * @param configurable The data for the process
     * @param uniqueID     The unique id which should get used for the process
     * @param displayName  The display name of the new process
     * @param maxMemory    The maximum amount of memory which the new process is allowed to use
     * @param port         The port of the new process (If it's already in use a random port will be used)
     * @param id           The id of the process (for example {@code 1}). The name of the process will also use
     *                     the given id (for {@code 1} it might be {@code Lobby-1}). If the id is already taken
     *                     a random id will get used
     * @param maxPlayers   The maximum amount of player which are allowed to join the process
     * @param inclusions   The inclusions which should get loaded before the start of the process
     * @return The created {@link ProcessInformation}
     */
    @Nullable
    default ProcessInformation startProcess(@NotNull String groupName, @Nullable String template,
                                            @NotNull JsonConfiguration configurable, @NotNull UUID uniqueID,
                                            @Nullable String displayName, @Nullable Integer maxMemory,
                                            @Nullable Integer port, @Nullable Integer id, @Nullable Integer maxPlayers,
                                            @NotNull Collection<ProcessInclusion> inclusions) {
        ProcessConfigurationBuilder builder = ProcessConfigurationBuilder
                .newBuilder(groupName)
                .extra(configurable)
                .uniqueId(uniqueID)
                .inclusions(inclusions);

        if (template != null) {
            builder.template(template);
        }

        if (id != null && id > 0) {
            builder.id(id);
        }

        if (displayName != null) {
            builder.displayName(displayName);
        }

        if (maxMemory != null && maxMemory > 100) {
            builder.maxMemory(maxMemory);
        }

        if (port != null && port > 0) {
            builder.port(port);
        }

        if (maxPlayers != null && maxPlayers > 0) {
            builder.maxPlayers(maxPlayers);
        }

        return this.startProcess(builder.build());
    }

    /**
     * Starts a process based on the given configuration
     *
     * @param configuration The configuration which is the base for the new process
     * @return The created {@link ProcessInformation}
     * @see ProcessConfigurationBuilder#newBuilder(String)
     */
    @Nullable
    ProcessInformation startProcess(@NotNull ProcessConfiguration configuration);

    /**
     * Starts a prepared process
     *
     * @param processInformation The process information of the prepared process
     * @return The process information after the start call
     */
    @NotNull
    ProcessInformation startProcess(@NotNull ProcessInformation processInformation);

    /**
     * Prepares a process
     *
     * @param groupName The name of the group which should be started from
     * @return The created {@link ProcessInformation}
     */
    @Nullable
    default ProcessInformation prepareProcess(@NotNull String groupName) {
        return this.prepareProcess(groupName, null);
    }

    /**
     * Prepares a process
     *
     * @param groupName The name of the group which should be started from
     * @param template  The template which should be used
     * @return The created {@link ProcessInformation}
     */
    @Nullable
    default ProcessInformation prepareProcess(@NotNull String groupName, @Nullable String template) {
        return this.prepareProcess(groupName, template, new JsonConfiguration());
    }

    /**
     * Prepares a process
     *
     * @param groupName    The name of the group which should be started from
     * @param template     The template which should be used
     * @param configurable The data for the process
     * @return The created {@link ProcessInformation}
     */
    @Nullable
    default ProcessInformation prepareProcess(@NotNull String groupName, @Nullable String template, @NotNull JsonConfiguration configurable) {
        return this.prepareProcess(groupName, template, configurable, UUID.randomUUID());
    }

    /**
     * Prepares a process
     *
     * @param groupName    The name of the group which should be started from
     * @param template     The template which should be used
     * @param configurable The data for the process
     * @param uniqueID     The unique id which should get used for the process
     * @return The created {@link ProcessInformation}
     */
    @Nullable
    default ProcessInformation prepareProcess(@NotNull String groupName, @Nullable String template,
                                              @NotNull JsonConfiguration configurable, @NotNull UUID uniqueID) {
        return this.prepareProcess(groupName, template, configurable, uniqueID, null);
    }

    /**
     * Prepares a process
     *
     * @param groupName    The name of the group which should be started from
     * @param template     The template which should be used
     * @param configurable The data for the process
     * @param uniqueID     The unique id which should get used for the process
     * @param displayName  The display name of the new process
     * @return The created {@link ProcessInformation}
     */
    @Nullable
    default ProcessInformation prepareProcess(@NotNull String groupName, @Nullable String template,
                                              @NotNull JsonConfiguration configurable, @NotNull UUID uniqueID,
                                              @Nullable String displayName) {
        return this.prepareProcess(groupName, template, configurable, uniqueID, displayName, null);
    }

    /**
     * Prepares a process
     *
     * @param groupName    The name of the group which should be started from
     * @param template     The template which should be used
     * @param configurable The data for the process
     * @param uniqueID     The unique id which should get used for the process
     * @param displayName  The display name of the new process
     * @param maxMemory    The maximum amount of memory which the new process is allowed to use
     * @return The created {@link ProcessInformation}
     */
    @Nullable
    default ProcessInformation prepareProcess(@NotNull String groupName, @Nullable String template,
                                              @NotNull JsonConfiguration configurable, @NotNull UUID uniqueID,
                                              @Nullable String displayName, @Nullable Integer maxMemory) {
        return this.prepareProcess(groupName, template, configurable, uniqueID, displayName, maxMemory, null);
    }

    /**
     * Prepares a process
     *
     * @param groupName    The name of the group which should be started from
     * @param template     The template which should be used
     * @param configurable The data for the process
     * @param uniqueID     The unique id which should get used for the process
     * @param displayName  The display name of the new process
     * @param maxMemory    The maximum amount of memory which the new process is allowed to use
     * @param port         The port of the new process (If it's already in use a random port will be used)
     * @return The created {@link ProcessInformation}
     */
    @Nullable
    default ProcessInformation prepareProcess(@NotNull String groupName, @Nullable String template,
                                              @NotNull JsonConfiguration configurable, @NotNull UUID uniqueID,
                                              @Nullable String displayName, @Nullable Integer maxMemory,
                                              @Nullable Integer port) {
        return this.prepareProcess(groupName, template, configurable, uniqueID, displayName, maxMemory, port, null);
    }

    /**
     * Prepares a process
     *
     * @param groupName    The name of the group which should be started from
     * @param template     The template which should be used
     * @param configurable The data for the process
     * @param uniqueID     The unique id which should get used for the process
     * @param displayName  The display name of the new process
     * @param maxMemory    The maximum amount of memory which the new process is allowed to use
     * @param port         The port of the new process (If it's already in use a random port will be used)
     * @param id           The id of the process (for example {@code 1}). The name of the process will also use
     *                     the given id (for {@code 1} it might be {@code Lobby-1}). If the id is already taken
     *                     a random id will get used
     * @return The created {@link ProcessInformation}
     */
    @Nullable
    default ProcessInformation prepareProcess(@NotNull String groupName, @Nullable String template,
                                              @NotNull JsonConfiguration configurable, @NotNull UUID uniqueID,
                                              @Nullable String displayName, @Nullable Integer maxMemory,
                                              @Nullable Integer port, @Nullable Integer id) {
        return this.prepareProcess(groupName, template, configurable, uniqueID, displayName, maxMemory, port, id, null);
    }

    /**
     * Prepares a process
     *
     * @param groupName    The name of the group which should be started from
     * @param template     The template which should be used
     * @param configurable The data for the process
     * @param uniqueID     The unique id which should get used for the process
     * @param displayName  The display name of the new process
     * @param maxMemory    The maximum amount of memory which the new process is allowed to use
     * @param port         The port of the new process (If it's already in use a random port will be used)
     * @param id           The id of the process (for example {@code 1}). The name of the process will also use
     *                     the given id (for {@code 1} it might be {@code Lobby-1}). If the id is already taken
     *                     a random id will get used
     * @param maxPlayers   The maximum amount of player which are allowed to join the process
     * @return The created {@link ProcessInformation}
     */
    @Nullable
    default ProcessInformation prepareProcess(@NotNull String groupName, @Nullable String template,
                                              @NotNull JsonConfiguration configurable, @NotNull UUID uniqueID,
                                              @Nullable String displayName, @Nullable Integer maxMemory,
                                              @Nullable Integer port, @Nullable Integer id, @Nullable Integer maxPlayers) {
        return this.prepareProcess(groupName, template, configurable, uniqueID, displayName, maxMemory, port, id, maxPlayers, new ArrayList<>());
    }

    /**
     * Prepares a process
     *
     * @param groupName    The name of the group which should be started from
     * @param template     The template which should be used
     * @param configurable The data for the process
     * @param uniqueID     The unique id which should get used for the process
     * @param displayName  The display name of the new process
     * @param maxMemory    The maximum amount of memory which the new process is allowed to use
     * @param port         The port of the new process (If it's already in use a random port will be used)
     * @param id           The id of the process (for example {@code 1}). The name of the process will also use
     *                     the given id (for {@code 1} it might be {@code Lobby-1}). If the id is already taken
     *                     a random id will get used
     * @param maxPlayers   The maximum amount of player which are allowed to join the process
     * @param inclusions   The inclusions which should get loaded before the start of the process
     * @return The created {@link ProcessInformation}
     */
    @Nullable
    default ProcessInformation prepareProcess(@NotNull String groupName, @Nullable String template,
                                              @NotNull JsonConfiguration configurable, @NotNull UUID uniqueID,
                                              @Nullable String displayName, @Nullable Integer maxMemory,
                                              @Nullable Integer port, @Nullable Integer id, @Nullable Integer maxPlayers,
                                              @NotNull Collection<ProcessInclusion> inclusions) {
        ProcessConfigurationBuilder builder = ProcessConfigurationBuilder
                .newBuilder(groupName)
                .extra(configurable)
                .uniqueId(uniqueID)
                .inclusions(inclusions);

        if (template != null) {
            builder.template(template);
        }

        if (id != null && id > 0) {
            builder.id(id);
        }

        if (displayName != null) {
            builder.displayName(displayName);
        }

        if (maxMemory != null && maxMemory > 100) {
            builder.maxMemory(maxMemory);
        }

        if (port != null && port > 0) {
            builder.port(port);
        }

        if (maxPlayers != null && maxPlayers > 0) {
            builder.maxPlayers(maxPlayers);
        }

        return this.prepareProcess(builder.build());
    }

    /**
     * Prepares a process based on the given configuration
     *
     * @param configuration The configuration which is the base for the new process
     * @return The created {@link ProcessInformation}
     * @see ProcessConfigurationBuilder#newBuilder(String)
     */
    @Nullable
    ProcessInformation prepareProcess(@NotNull ProcessConfiguration configuration);

    /**
     * Stops a process
     *
     * @param name The name of the process
     * @return The last known {@link ProcessInformation}
     */
    @Nullable
    ProcessInformation stopProcess(@NotNull String name);

    /**
     * Stops a process
     *
     * @param uniqueID The uniqueID of the process
     * @return The last {@link ProcessInformation}
     */
    @Nullable
    ProcessInformation stopProcess(@NotNull UUID uniqueID);

    /**
     * Gets a process
     *
     * @param name The name of the process
     * @return The {@link ProcessInformation} of the process or {@code null} if the process does not exists
     */
    @Nullable
    ProcessInformation getProcess(@NotNull String name);

    /**
     * Gets a process
     *
     * @param uniqueID The uniqueID of the process
     * @return The {@link ProcessInformation} of the process or {@code null} if the process does not exists
     */
    @Nullable
    ProcessInformation getProcess(@NotNull UUID uniqueID);

    /**
     * Get all processes
     *
     * @return All started processes
     */
    @NotNull
    List<ProcessInformation> getAllProcesses();

    /**
     * Get all processes of a specific group
     *
     * @param group The group which should be searched for
     * @return All started processes of the specified groups
     */
    @NotNull
    List<ProcessInformation> getProcesses(@NotNull String group);

    /**
     * Executes a command on a process
     *
     * @param name        The name of the process
     * @param commandLine The command line with should be executed
     */
    void executeProcessCommand(@NotNull String name, @NotNull String commandLine);

    /**
     * Gets the global online count
     *
     * @param ignoredProxies The ignored proxies
     * @return The global online count
     */
    int getGlobalOnlineCount(@NotNull Collection<String> ignoredProxies);

    /**
     * Get the current process information
     *
     * @return The current {@link ProcessInformation} or {@code null} if the runtime is not a process
     * @deprecated Has been moved to {@link API#getCurrentProcessInformation()}. Will be removed in a further release
     */
    @Nullable
    @ApiStatus.ScheduledForRemoval(inVersion = "2.3")
    @Deprecated
    ProcessInformation getThisProcessInformation();

    /**
     * Iterates through all {@link ProcessInformation}
     *
     * @param action The consumer which will accept by each {@link ProcessInformation}
     */
    default void forEach(@NotNull Consumer<ProcessInformation> action) {
        getAllProcesses().forEach(action);
    }

    /**
     * Updates a specific {@link ProcessInformation}
     *
     * @param processInformation The process information which should be updated
     */
    void update(@NotNull ProcessInformation processInformation);
}
