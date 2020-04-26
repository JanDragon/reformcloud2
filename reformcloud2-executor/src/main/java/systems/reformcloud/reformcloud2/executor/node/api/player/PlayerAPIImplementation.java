package systems.reformcloud.reformcloud2.executor.node.api.player;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.common.api.basic.packets.api.*;
import systems.reformcloud.reformcloud2.executor.api.common.api.player.PlayerAsyncAPI;
import systems.reformcloud.reformcloud2.executor.api.common.api.player.PlayerSyncAPI;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.manager.DefaultChannelManager;
import systems.reformcloud.reformcloud2.executor.api.common.process.ProcessInformation;
import systems.reformcloud.reformcloud2.executor.api.common.utility.list.Streams;
import systems.reformcloud.reformcloud2.executor.api.common.utility.task.Task;
import systems.reformcloud.reformcloud2.executor.api.common.utility.task.defaults.DefaultTask;
import systems.reformcloud.reformcloud2.executor.api.network.api.*;
import systems.reformcloud.reformcloud2.executor.api.node.network.NodeNetworkManager;

import java.util.UUID;

public class PlayerAPIImplementation implements PlayerAsyncAPI, PlayerSyncAPI {

    public PlayerAPIImplementation(NodeNetworkManager nodeNetworkManager) {
        this.nodeNetworkManager = nodeNetworkManager;
    }

    private final NodeNetworkManager nodeNetworkManager;

    @NotNull
    @Override
    public Task<Void> sendMessageAsync(@NotNull UUID player, @NotNull String message) {
        Task<Void> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> {
            ProcessInformation processInformation = this.getPlayerOnProxy(player);
            if (processInformation == null) {
                task.complete(null);
                return;
            }

            if (this.nodeNetworkManager.getCluster().getSelfNode().getName().equals(processInformation.getProcessDetail().getParentName())) {
                DefaultChannelManager.INSTANCE
                        .get(processInformation.getProcessDetail().getName())
                        .ifPresent(e -> e.sendPacket(new PacketAPISendMessage(player, message)));
            } else {
                DefaultChannelManager.INSTANCE
                        .get(processInformation.getProcessDetail().getParentName())
                        .ifPresent(e -> e.sendPacket(new PacketAPIActionSendMessage(player, message)));
            }

            task.complete(null);
        });
        return task;
    }

    @NotNull
    @Override
    public Task<Void> kickPlayerAsync(@NotNull UUID player, @NotNull String message) {
        Task<Void> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> {
            ProcessInformation processInformation = this.getPlayerOnProxy(player);
            if (processInformation == null) {
                task.complete(null);
                return;
            }

            if (this.nodeNetworkManager.getCluster().getSelfNode().getName().equals(processInformation.getProcessDetail().getParentName())) {
                DefaultChannelManager.INSTANCE
                        .get(processInformation.getProcessDetail().getName())
                        .ifPresent(e -> e.sendPacket(new PacketAPIKickPlayer(player, message)));
            } else {
                DefaultChannelManager.INSTANCE
                        .get(processInformation.getProcessDetail().getParentName())
                        .ifPresent(e -> e.sendPacket(new PacketAPIActionKickPlayer(player, message)));
            }

            task.complete(null);
        });
        return task;
    }

    @NotNull
    @Override
    public Task<Void> kickPlayerFromServerAsync(@NotNull UUID player, @NotNull String message) {
        Task<Void> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> {
            ProcessInformation processInformation = this.getPlayerOnServer(player);
            if (processInformation == null) {
                task.complete(null);
                return;
            }

            if (this.nodeNetworkManager.getCluster().getSelfNode().getName().equals(processInformation.getProcessDetail().getParentName())) {
                DefaultChannelManager.INSTANCE
                        .get(processInformation.getProcessDetail().getName())
                        .ifPresent(e -> e.sendPacket(new PacketAPIKickPlayer(player, message)));
            } else {
                DefaultChannelManager.INSTANCE
                        .get(processInformation.getProcessDetail().getParentName())
                        .ifPresent(e -> e.sendPacket(new PacketAPIActionKickPlayerFromServer(player, message)));
            }

            task.complete(null);
        });
        return task;
    }

    @NotNull
    @Override
    public Task<Void> playSoundAsync(@NotNull UUID player, @NotNull String sound, float f1, float f2) {
        Task<Void> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> {
            ProcessInformation processInformation = this.getPlayerOnServer(player);
            if (processInformation == null) {
                task.complete(null);
                return;
            }

            if (this.nodeNetworkManager.getCluster().getSelfNode().getName().equals(processInformation.getProcessDetail().getParentName())) {
                DefaultChannelManager.INSTANCE
                        .get(processInformation.getProcessDetail().getName())
                        .ifPresent(e -> e.sendPacket(new PacketAPIPlaySound(player, sound, f1, f2)));
            } else {
                DefaultChannelManager.INSTANCE
                        .get(processInformation.getProcessDetail().getParentName())
                        .ifPresent(e -> e.sendPacket(new PacketAPIActionPlaySound(player, sound, f1, f2)));
            }

            task.complete(null);
        });
        return task;
    }

    @NotNull
    @Override
    public Task<Void> sendTitleAsync(@NotNull UUID player, @NotNull String title, @NotNull String subTitle, int fadeIn, int stay, int fadeOut) {
        Task<Void> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> {
            ProcessInformation processInformation = this.getPlayerOnProxy(player);
            if (processInformation == null) {
                task.complete(null);
                return;
            }

            if (this.nodeNetworkManager.getCluster().getSelfNode().getName().equals(processInformation.getProcessDetail().getParentName())) {
                DefaultChannelManager.INSTANCE
                        .get(processInformation.getProcessDetail().getName())
                        .ifPresent(e -> e.sendPacket(new PacketAPISendTitle(player, title, subTitle, fadeIn, stay, fadeOut)));
            } else {
                DefaultChannelManager.INSTANCE
                        .get(processInformation.getProcessDetail().getParentName())
                        .ifPresent(e -> e.sendPacket(new PacketAPIActionSendTitle(player, title, subTitle, fadeIn, stay, fadeOut)));
            }

            task.complete(null);
        });
        return task;
    }

    @NotNull
    @Override
    public Task<Void> playEffectAsync(@NotNull UUID player, @NotNull String entityEffect) {
        Task<Void> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> {
            ProcessInformation processInformation = this.getPlayerOnServer(player);
            if (processInformation == null) {
                task.complete(null);
                return;
            }

            if (this.nodeNetworkManager.getCluster().getSelfNode().getName().equals(processInformation.getProcessDetail().getParentName())) {
                DefaultChannelManager.INSTANCE
                        .get(processInformation.getProcessDetail().getName())
                        .ifPresent(e -> e.sendPacket(new PacketAPIPlayEntityEffect(player, entityEffect)));
            } else {
                DefaultChannelManager.INSTANCE
                        .get(processInformation.getProcessDetail().getParentName())
                        .ifPresent(e -> e.sendPacket(new PacketAPIActionPlayEntityEffect(player, entityEffect)));
            }

            task.complete(null);
        });
        return task;
    }

    @NotNull
    @Override
    public Task<Void> teleportAsync(@NotNull UUID player, @NotNull String world, double x, double y, double z, float yaw, float pitch) {
        Task<Void> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> {
            ProcessInformation processInformation = this.getPlayerOnServer(player);
            if (processInformation == null) {
                task.complete(null);
                return;
            }

            if (this.nodeNetworkManager.getCluster().getSelfNode().getName().equals(processInformation.getProcessDetail().getParentName())) {
                DefaultChannelManager.INSTANCE
                        .get(processInformation.getProcessDetail().getName())
                        .ifPresent(e -> e.sendPacket(new PacketAPITeleportPlayer(player, world, x, y, z, yaw, pitch)));
            } else {
                DefaultChannelManager.INSTANCE
                        .get(processInformation.getProcessDetail().getParentName())
                        .ifPresent(e -> e.sendPacket(new PacketAPIActionTeleportPlayer(player, world, x, y, z, yaw, pitch)));
            }

            task.complete(null);
        });
        return task;
    }

    @NotNull
    @Override
    public Task<Void> connectAsync(@NotNull UUID player, @NotNull String server) {
        Task<Void> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> {
            ProcessInformation processInformation = this.getPlayerOnProxy(player);
            if (processInformation == null) {
                task.complete(null);
                return;
            }

            if (this.nodeNetworkManager.getCluster().getSelfNode().getName().equals(processInformation.getProcessDetail().getParentName())) {
                DefaultChannelManager.INSTANCE
                        .get(processInformation.getProcessDetail().getName())
                        .ifPresent(e -> e.sendPacket(new PacketAPIConnectPlayerToServer(player, server)));
            } else {
                DefaultChannelManager.INSTANCE
                        .get(processInformation.getProcessDetail().getParentName())
                        .ifPresent(e -> e.sendPacket(new PacketAPIActionConnectPlayerToServer(player, server)));
            }

            task.complete(null);
        });
        return task;
    }

    @NotNull
    @Override
    public Task<Void> connectAsync(@NotNull UUID player, @NotNull ProcessInformation server) {
        return connectAsync(player, server.getProcessDetail().getName());
    }

    @NotNull
    @Override
    public Task<Void> connectAsync(@NotNull UUID player, @NotNull UUID target) {
        Task<Void> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> {
            ProcessInformation targetServer = getPlayerOnServer(target);
            if (targetServer != null) {
                connectAsync(player, targetServer).awaitUninterruptedly();
            }

            task.complete(null);
        });
        return task;
    }

    @Override
    public void sendMessage(@NotNull UUID player, @NotNull String message) {
        sendMessageAsync(player, message).awaitUninterruptedly();
    }

    @Override
    public void kickPlayer(@NotNull UUID player, @NotNull String message) {
        kickPlayerAsync(player, message).awaitUninterruptedly();
    }

    @Override
    public void kickPlayerFromServer(@NotNull UUID player, @NotNull String message) {
        kickPlayerFromServerAsync(player, message).awaitUninterruptedly();
    }

    @Override
    public void playSound(@NotNull UUID player, @NotNull String sound, float f1, float f2) {
        playSoundAsync(player, sound, f1, f2).awaitUninterruptedly();
    }

    @Override
    public void sendTitle(@NotNull UUID player, @NotNull String title, @NotNull String subTitle, int fadeIn, int stay, int fadeOut) {
        sendTitleAsync(player, title, subTitle, fadeIn, stay, fadeOut).awaitUninterruptedly();
    }

    @Override
    public void playEffect(@NotNull UUID player, @NotNull String entityEffect) {
        playEffectAsync(player, entityEffect).awaitUninterruptedly();
    }

    @Override
    public void teleport(@NotNull UUID player, @NotNull String world, double x, double y, double z, float yaw, float pitch) {
        teleportAsync(player, world, x, y, z, yaw, pitch).awaitUninterruptedly();
    }

    @Override
    public void connect(@NotNull UUID player, @NotNull String server) {
        connectAsync(player, server).awaitUninterruptedly();
    }

    @Override
    public void connect(@NotNull UUID player, @NotNull ProcessInformation server) {
        connectAsync(player, server).awaitUninterruptedly();
    }

    @Override
    public void connect(@NotNull UUID player, @NotNull UUID target) {
        connectAsync(player, target).awaitUninterruptedly();
    }

    private ProcessInformation getPlayerOnProxy(UUID uniqueID) {
        return Streams.filter(
                this.nodeNetworkManager.getNodeProcessHelper().getClusterProcesses(),
                processInformation -> !processInformation.getProcessDetail().getTemplate().isServer()
                        && Streams.filterToReference(processInformation.getProcessPlayerManager().getOnlinePlayers(),
                        player -> player.getUniqueID().equals(uniqueID)).isPresent()
        );
    }

    private ProcessInformation getPlayerOnServer(UUID uniqueID) {
        return Streams.filter(this.nodeNetworkManager.getNodeProcessHelper().getClusterProcesses(), processInformation -> processInformation.getProcessDetail().getTemplate().isServer() && Streams.filterToReference(processInformation.getProcessPlayerManager().getOnlinePlayers(), player -> player.getUniqueID().equals(uniqueID)).isPresent());
    }
}
