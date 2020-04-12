package systems.reformcloud.reformcloud2.executor.api.spigot;

import com.google.common.base.Enums;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.ExecutorType;
import systems.reformcloud.reformcloud2.executor.api.api.API;
import systems.reformcloud.reformcloud2.executor.api.common.ExecutorAPI;
import systems.reformcloud.reformcloud2.executor.api.common.api.basic.ExternalEventBusHandler;
import systems.reformcloud.reformcloud2.executor.api.common.api.basic.events.ProcessUpdatedEvent;
import systems.reformcloud.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.event.EventManager;
import systems.reformcloud.reformcloud2.executor.api.common.event.basic.DefaultEventManager;
import systems.reformcloud.reformcloud2.executor.api.common.event.handler.Listener;
import systems.reformcloud.reformcloud2.executor.api.common.groups.messages.IngameMessages;
import systems.reformcloud.reformcloud2.executor.api.common.network.challenge.shared.ClientChallengeAuthHandler;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.PacketSender;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.manager.DefaultChannelManager;
import systems.reformcloud.reformcloud2.executor.api.common.network.client.DefaultNetworkClient;
import systems.reformcloud.reformcloud2.executor.api.common.network.client.NetworkClient;
import systems.reformcloud.reformcloud2.executor.api.common.network.messaging.ProxiedChannelMessageHandler;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.defaults.DefaultPacketHandler;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.handler.PacketHandler;
import systems.reformcloud.reformcloud2.executor.api.common.process.ProcessInformation;
import systems.reformcloud.reformcloud2.executor.api.common.process.ProcessState;
import systems.reformcloud.reformcloud2.executor.api.common.utility.system.SystemHelper;
import systems.reformcloud.reformcloud2.executor.api.common.utility.task.Task;
import systems.reformcloud.reformcloud2.executor.api.common.utility.thread.AbsoluteThread;
import systems.reformcloud.reformcloud2.executor.api.executor.PlayerAPIExecutor;
import systems.reformcloud.reformcloud2.executor.api.network.channel.APINetworkChannelReader;
import systems.reformcloud.reformcloud2.executor.api.network.packets.in.APIPacketInAPIAction;
import systems.reformcloud.reformcloud2.executor.api.network.packets.in.APIPacketInPluginAction;
import systems.reformcloud.reformcloud2.executor.api.network.packets.out.APIBungeePacketOutRequestIngameMessages;
import systems.reformcloud.reformcloud2.executor.api.spigot.plugins.PluginExecutorContainer;

import java.io.File;
import java.util.UUID;

public final class SpigotExecutor extends API implements PlayerAPIExecutor {

    private static SpigotExecutor instance;

    private final JavaPlugin plugin;

    private final PacketHandler packetHandler = new DefaultPacketHandler();

    private final NetworkClient networkClient = new DefaultNetworkClient();

    private IngameMessages messages = new IngameMessages();

    private ProcessInformation thisProcessInformation;

    SpigotExecutor(JavaPlugin plugin) {
        super.type = ExecutorType.API;

        instance = this;
        this.plugin = plugin;
        new ExternalEventBusHandler(packetHandler, new DefaultEventManager());
        getEventManager().registerListener(this);

        packetHandler.registerHandler(new APIPacketInAPIAction(this));
        packetHandler.registerHandler(new APIPacketInPluginAction(new PluginExecutorContainer()));
        packetHandler.registerHandler(new ProxiedChannelMessageHandler());

        String connectionKey = JsonConfiguration.read("reformcloud/.connection/key.json").getString("key");
        SystemHelper.deleteFile(new File("reformcloud/.connection/key.json"));
        JsonConfiguration connectionConfig = JsonConfiguration.read("reformcloud/.connection/connection.json");

        this.thisProcessInformation = connectionConfig.get("startInfo", ProcessInformation.TYPE);
        if (thisProcessInformation == null) {
            System.exit(0);
            return;
        }

        this.networkClient.connect(
                connectionConfig.getString("controller-host"),
                connectionConfig.getInteger("controller-port"),
                () -> new APINetworkChannelReader(this.packetHandler),
                new ClientChallengeAuthHandler(
                        connectionKey,
                        thisProcessInformation.getProcessDetail().getName(),
                        () -> new JsonConfiguration(),
                        context -> {
                        } // unused here
                )
        );
        ExecutorAPI.setInstance(this);
        awaitConnectionAndUpdate();
    }

    public JavaPlugin getPlugin() {
        return plugin;
    }

    public IngameMessages getMessages() {
        return messages;
    }

    @NotNull
    @Override
    public ProcessInformation getCurrentProcessInformation() {
        return this.thisProcessInformation;
    }

    @NotNull
    public static SpigotExecutor getInstance() {
        return instance;
    }

    @NotNull
    public EventManager getEventManager() {
        return ExternalEventBusHandler.getInstance().getEventManager();
    }

    NetworkClient getNetworkClient() {
        return networkClient;
    }

    @Override
    public PacketHandler packetHandler() {
        return packetHandler;
    }

    @Listener
    public void handle(final ProcessUpdatedEvent event) {
        if (event.getProcessInformation().getProcessDetail().getProcessUniqueID().equals(thisProcessInformation.getProcessDetail().getProcessUniqueID())) {
            thisProcessInformation = event.getProcessInformation();
        }
    }

    @Override
    @Deprecated
    public ProcessInformation getThisProcessInformation() {
        return getCurrentProcessInformation();
    }

    private void awaitConnectionAndUpdate() {
        Task.EXECUTOR.execute(() -> {
            PacketSender packetSender = DefaultChannelManager.INSTANCE.get("Controller").orElse(null);
            while (packetSender == null) {
                packetSender = DefaultChannelManager.INSTANCE.get("Controller").orElse(null);
                AbsoluteThread.sleep(100);
            }

            thisProcessInformation.updateMaxPlayers(Bukkit.getMaxPlayers());
            thisProcessInformation.getNetworkInfo().setConnected(true);
            thisProcessInformation.getProcessDetail().setProcessState(ProcessState.READY);
            thisProcessInformation.updateRuntimeInformation();
            ExecutorAPI.getInstance().getSyncAPI().getProcessSyncAPI().update(thisProcessInformation);

            DefaultChannelManager.INSTANCE.get("Controller").ifPresent(controller -> packetHandler.getQueryHandler().sendQueryAsync(controller, new APIBungeePacketOutRequestIngameMessages()).onComplete(packet -> {
                SpigotExecutor.this.messages = packet.content().get("messages", IngameMessages.TYPE);
            }));
        });
    }

    public void setThisProcessInformation(ProcessInformation thisProcessInformation) {
        this.thisProcessInformation = thisProcessInformation;
    }

    /* ======================== Player API ======================== */

    @Override
    public void executeSendMessage(UUID player, String message) {
        Player player1 = Bukkit.getPlayer(player);
        if (player1 != null) {
            player1.sendMessage(message);
        }
    }

    @Override
    public void executeKickPlayer(UUID player, String message) {
        Player player1 = Bukkit.getPlayer(player);
        if (player1 != null) {
            Bukkit.getScheduler().runTask(plugin, () -> player1.kickPlayer(message));
        }
    }

    @Override
    public void executePlaySound(UUID player, String sound, float f1, float f2) {
        Player player1 = Bukkit.getPlayer(player);
        Sound sound1 = Enums.getIfPresent(Sound.class, sound).orNull();
        if (player1 != null && sound1 != null) {
            Bukkit.getScheduler().runTask(plugin, () -> player1.playSound(player1.getLocation(), sound1, f1, f2));
        }
    }

    @Override
    public void executeSendTitle(UUID player, String title, String subTitle, int fadeIn, int stay, int fadeOut) {
        Player player1 = Bukkit.getPlayer(player);
        if (player1 != null) {
            Bukkit.getScheduler().runTask(plugin, () -> player1.sendTitle(title, subTitle, fadeIn, stay, fadeOut));
        }
    }

    @Override
    public void executePlayEffect(UUID player, String entityEffect) {
        Player player1 = Bukkit.getPlayer(player);
        EntityEffect entityEffect1 = Enums.getIfPresent(EntityEffect.class, entityEffect).orNull();
        if (player1 != null && entityEffect1 != null) {
            Bukkit.getScheduler().runTask(plugin, () -> player1.playEffect(entityEffect1));
        }
    }

    @Override
    public <T> void executePlayEffect(UUID player, String effect, T data) {
        Player player1 = Bukkit.getPlayer(player);
        Effect effect1 = Enums.getIfPresent(Effect.class, effect).orNull();
        if (player1 != null && effect1 != null) {
            Bukkit.getScheduler().runTask(plugin, () -> player1.playEffect(player1.getLocation(), effect1, data));
        }
    }

    @Override
    public void executeRespawn(UUID player) {
        Player player1 = Bukkit.getPlayer(player);
        if (player1 != null) {
            Bukkit.getScheduler().runTask(plugin, () -> player1.spigot().respawn());
        }
    }

    @Override
    public void executeTeleport(UUID player, String world, double x, double y, double z, float yaw, float pitch) {
        Player player1 = Bukkit.getPlayer(player);
        if (player1 != null) {
            Bukkit.getScheduler().runTask(plugin, () -> player1.teleport(new Location(Bukkit.getWorld(world), x, y, z, yaw, pitch)));
        }
    }

    @Override
    public void executeConnect(UUID player, String server) {
        throw new UnsupportedOperationException("Not supported on spigot");
    }

    @Override
    public void executeConnect(UUID player, ProcessInformation server) {
        throw new UnsupportedOperationException("Not supported on spigot");
    }

    @Override
    public void executeConnect(UUID player, UUID target) {
        throw new UnsupportedOperationException("Not supported on spigot");
    }

    @Override
    public void executeSetResourcePack(UUID player, String pack) {
        Player player1 = Bukkit.getPlayer(player);
        if (player1 != null) {
            Bukkit.getScheduler().runTask(plugin, () -> player1.setResourcePack(pack));
        }
    }
}
