/*
 * MIT License
 *
 * Copyright (c) ReformCloud-Team
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package systems.reformcloud.reformcloud2.executor.api.velocity.event;

import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.ResultedEvent;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.connection.LoginEvent;
import com.velocitypowered.api.event.connection.PostLoginEvent;
import com.velocitypowered.api.event.player.KickedFromServerEvent;
import com.velocitypowered.api.event.player.ServerPreConnectEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import net.kyori.text.serializer.legacy.LegacyComponentSerializer;
import systems.reformcloud.reformcloud2.executor.api.api.API;
import systems.reformcloud.reformcloud2.executor.api.common.CommonHelper;
import systems.reformcloud.reformcloud2.executor.api.common.ExecutorAPI;
import systems.reformcloud.reformcloud2.executor.api.common.groups.utils.PlayerAccessConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.PacketSender;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.manager.DefaultChannelManager;
import systems.reformcloud.reformcloud2.executor.api.common.process.ProcessInformation;
import systems.reformcloud.reformcloud2.executor.api.common.process.ProcessState;
import systems.reformcloud.reformcloud2.executor.api.common.utility.thread.AbsoluteThread;
import systems.reformcloud.reformcloud2.executor.api.network.packets.out.APIBungeePacketOutPlayerServerSwitch;
import systems.reformcloud.reformcloud2.executor.api.network.packets.out.APIPacketOutLogoutPlayer;
import systems.reformcloud.reformcloud2.executor.api.network.packets.out.APIPacketOutPlayerLoggedIn;
import systems.reformcloud.reformcloud2.executor.api.velocity.VelocityExecutor;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

public final class PlayerListenerHandler {

    @Subscribe(order = PostOrder.FIRST)
    public void handle(final ServerPreConnectEvent event) {
        final Player player = event.getPlayer();
        if (!player.getCurrentServer().isPresent()) {
            ProcessInformation lobby = VelocityExecutor.getBestLobbyForPlayer(
                    API.getInstance().getCurrentProcessInformation(),
                    player::hasPermission,
                    null
            );
            if (lobby != null) {
                Optional<RegisteredServer> server = VelocityExecutor.getInstance().getProxyServer().getServer(lobby.getProcessDetail().getName());
                if (!server.isPresent()) {
                    event.setResult(ServerPreConnectEvent.ServerResult.denied());
                    return;
                }

                event.setResult(ServerPreConnectEvent.ServerResult.allowed(server.get()));
            } else {
                event.setResult(ServerPreConnectEvent.ServerResult.denied());
            }
        }

        if (event.getResult().getServer().isPresent()) {
            DefaultChannelManager.INSTANCE.get("Controller").ifPresent(sender -> sender.sendPacket(new APIBungeePacketOutPlayerServerSwitch(
                    event.getPlayer().getUniqueId(),
                    event.getPlayer().getCurrentServer().isPresent() ? event.getPlayer().getCurrentServer().get().getServerInfo().getName() : null,
                    event.getResult().getServer().get().getServerInfo().getName()
            )));
            AbsoluteThread.sleep(20);
        }
    }

    @Subscribe(order = PostOrder.FIRST)
    public void handle(final LoginEvent event) {
        PacketSender sender = DefaultChannelManager.INSTANCE.get("Controller").orElse(null);
        if (sender == null) {
            event.setResult(ResultedEvent.ComponentResult.denied(LegacyComponentSerializer.legacyLinking().deserialize("§4§lThe current proxy is not connected to the controller")));
        }
    }

    @Subscribe
    public void handle(final PostLoginEvent event) {
        final Player player = event.getPlayer();
        final ProcessInformation current = API.getInstance().getCurrentProcessInformation();
        final PlayerAccessConfiguration configuration = current.getProcessGroup().getPlayerAccessConfiguration();

        if (configuration.isUseCloudPlayerLimit()
                && current.getProcessDetail().getMaxPlayers() < current.getProcessPlayerManager().getOnlineCount() + 1
                && !player.hasPermission(configuration.getFullJoinPermission())) {
            player.disconnect(LegacyComponentSerializer.legacyLinking().deserialize("§4§lThe proxy is full"));
            return;
        }

        if (configuration.isJoinOnlyPerPermission() && !player.hasPermission(configuration.getJoinPermission())) {
            player.disconnect(LegacyComponentSerializer.legacyLinking().deserialize("§4§lYou do not have permission to enter this proxy"));
            return;
        }

        if (configuration.isMaintenance() && !player.hasPermission(configuration.getMaintenanceJoinPermission())) {
            player.disconnect(LegacyComponentSerializer.legacyLinking().deserialize("§4§lThis proxy is currently in maintenance"));
            return;
        }

        if (current.getProcessDetail().getProcessState().equals(ProcessState.FULL) && !player.hasPermission(configuration.getFullJoinPermission())) {
            player.disconnect(LegacyComponentSerializer.legacyLinking().deserialize("§4§lYou are not allowed to join this server in the current state"));
            return;
        }

        if (!current.getProcessPlayerManager().onLogin(event.getPlayer().getUniqueId(), event.getPlayer().getUsername())) {
            player.disconnect(LegacyComponentSerializer.legacyLinking().deserialize("§4§lYou are not allowed to join this proxy"));
            return;
        }

        if (VelocityExecutor.getInstance().getProxyServer().getPlayerCount() >= current.getProcessDetail().getMaxPlayers()
                && !current.getProcessDetail().getProcessState().equals(ProcessState.FULL)
                && !current.getProcessDetail().getProcessState().equals(ProcessState.INVISIBLE)) {
            current.getProcessDetail().setProcessState(ProcessState.FULL);
        }

        current.updateRuntimeInformation();
        VelocityExecutor.getInstance().setThisProcessInformation(current); //Update it directly on the current host to prevent issues
        ExecutorAPI.getInstance().getSyncAPI().getProcessSyncAPI().update(current);

        CommonHelper.EXECUTOR.execute(() -> DefaultChannelManager.INSTANCE.get("Controller").ifPresent(packetSender -> packetSender.sendPacket(new APIPacketOutPlayerLoggedIn(event.getPlayer().getUsername()))));
    }

    @Subscribe(order = PostOrder.FIRST)
    public void handle(final KickedFromServerEvent event) {
        Player player = event.getPlayer();
        ProcessInformation lobby = VelocityExecutor.getBestLobbyForPlayer(
                API.getInstance().getCurrentProcessInformation(),
                player::hasPermission,
                event.getServer().getServerInfo().getName()
        );
        if (lobby != null) {
            Optional<RegisteredServer> server = VelocityExecutor.getInstance().getProxyServer().getServer(lobby.getProcessDetail().getName());
            if (!server.isPresent()) {
                event.setResult(KickedFromServerEvent.DisconnectPlayer.create(LegacyComponentSerializer.legacyLinking().deserialize(VelocityExecutor.getInstance().getMessages().format(
                        VelocityExecutor.getInstance().getMessages().getNoHubServerAvailable()
                ))));
                return;
            }

            event.setResult(KickedFromServerEvent.RedirectPlayer.create(server.get()));
            return;
        }

        event.setResult(KickedFromServerEvent.DisconnectPlayer.create(LegacyComponentSerializer.legacyLinking().deserialize(VelocityExecutor.getInstance().getMessages().format(
                VelocityExecutor.getInstance().getMessages().getNoHubServerAvailable()
        ))));
    }

    @Subscribe(order = PostOrder.FIRST)
    public void handle(final DisconnectEvent event) {
        DefaultChannelManager.INSTANCE.get("Controller").ifPresent(packetSender -> packetSender.sendPacket(new APIPacketOutLogoutPlayer(
                event.getPlayer().getUniqueId(),
                event.getPlayer().getUsername(),
                event.getPlayer().getCurrentServer().isPresent() ? event.getPlayer().getCurrentServer().get().getServerInfo().getName() : null
        )));

        VelocityExecutor.getInstance().getProxyServer().getScheduler()
                .buildTask(VelocityExecutor.getInstance().getPlugin(), () -> {
                    ProcessInformation current = API.getInstance().getCurrentProcessInformation();
                    if (VelocityExecutor.getInstance().getProxyServer().getPlayerCount() < current.getProcessDetail().getMaxPlayers()
                            && !current.getProcessDetail().getProcessState().equals(ProcessState.READY)
                            && !current.getProcessDetail().getProcessState().equals(ProcessState.INVISIBLE)) {
                        current.getProcessDetail().setProcessState(ProcessState.READY);
                    }

                    current.updateRuntimeInformation();
                    current.getProcessPlayerManager().onLogout(event.getPlayer().getUniqueId());
                    VelocityExecutor.getInstance().setThisProcessInformation(current);
                    ExecutorAPI.getInstance().getSyncAPI().getProcessSyncAPI().update(current);
                }).delay(20, TimeUnit.MILLISECONDS).schedule();
    }

    /* todo: wait until velocity 1.1.0 is published. The chat event is not fired when the player types in a command
       todo: see https://github.com/VelocityPowered/Velocity/blob/10680f16d376371401f4a203d94c888b22f24f14/proxy/src/main/java/com/velocitypowered/proxy/connection/client/ClientPlaySessionHandler.java#L115-L127
    @Subscribe
    public void handle(final PlayerChatEvent event) {
        if (API.getInstance().getCurrentProcessInformation().getProcessGroup().getPlayerAccessConfiguration().isPlayerControllerCommandReporting()
                && event.getMessage().startsWith("/")) {
            DefaultChannelManager.INSTANCE.get("Controller").ifPresent(packetSender -> packetSender.sendPacket(new APIPacketOutPlayerCommandExecute(
                    event.getPlayer().getUsername(),
                    event.getPlayer().getUniqueId(),
                    event.getMessage().replaceFirst("/", "")
            )));
        }
    }*/

}
