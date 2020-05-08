package systems.reformcloud.reformcloud2.commands.application;

import com.google.gson.reflect.TypeToken;
import org.jetbrains.annotations.Nullable;
import systems.reformcloud.reformcloud2.commands.application.listener.ProcessInclusionHandler;
import systems.reformcloud.reformcloud2.commands.application.packet.PacketGetCommandsConfig;
import systems.reformcloud.reformcloud2.commands.application.update.CommandAddonUpdater;
import systems.reformcloud.reformcloud2.commands.config.CommandsConfig;
import systems.reformcloud.reformcloud2.commands.plugin.packet.PacketReleaseCommandsConfig;
import systems.reformcloud.reformcloud2.executor.api.common.ExecutorAPI;
import systems.reformcloud.reformcloud2.executor.api.common.application.api.Application;
import systems.reformcloud.reformcloud2.executor.api.common.application.updater.ApplicationUpdateRepository;
import systems.reformcloud.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.network.NetworkUtil;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.manager.DefaultChannelManager;
import systems.reformcloud.reformcloud2.executor.api.common.utility.system.SystemHelper;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

public class ReformCloudApplication extends Application {

    private static ReformCloudApplication instance;

    private static CommandsConfig commandsConfig;

    private static final ApplicationUpdateRepository REPOSITORY = new CommandAddonUpdater();

    @Override
    public void onInstallable() {
        ExecutorAPI.getInstance().getEventManager().registerListener(new ProcessInclusionHandler());
    }

    @Override
    public void onLoad() {
        instance = this;
    }

    @Override
    public void onEnable() {
        final Path path = Paths.get(dataFolder().getPath(), "config.json");
        if (!Files.exists(path)) {
            SystemHelper.createDirectory(dataFolder().toPath());
            new JsonConfiguration()
                    .add("config", new CommandsConfig(
                            true, Arrays.asList("l", "leave", "lobby", "hub", "quit"),
                            true, Arrays.asList("reformcloud", "rc", "cloud")
                    )).write(path);
        }

        commandsConfig = JsonConfiguration.read(path).get("config", new TypeToken<CommandsConfig>() {
        });
        ExecutorAPI.getInstance().getPacketHandler().registerHandler(PacketGetCommandsConfig.class);

        DefaultChannelManager.INSTANCE.getAllSender().forEach(e -> e.sendPacket(new PacketReleaseCommandsConfig(commandsConfig)));
    }

    @Override
    public void onPreDisable() {
        ExecutorAPI.getInstance().getPacketHandler().unregisterNetworkHandler(NetworkUtil.EXTERNAL_BUS + 1);
    }

    @Nullable
    @Override
    public ApplicationUpdateRepository getUpdateRepository() {
        return REPOSITORY;
    }

    public static CommandsConfig getCommandsConfig() {
        return commandsConfig;
    }

    public static ReformCloudApplication getInstance() {
        return instance;
    }
}
