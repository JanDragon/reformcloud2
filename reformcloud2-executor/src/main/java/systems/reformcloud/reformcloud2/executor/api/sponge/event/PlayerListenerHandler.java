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
package systems.reformcloud.reformcloud2.executor.api.sponge.event;

import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.text.Text;
import systems.reformcloud.reformcloud2.executor.api.api.API;
import systems.reformcloud.reformcloud2.executor.api.common.ExecutorAPI;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.PacketSender;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.manager.DefaultChannelManager;
import systems.reformcloud.reformcloud2.executor.api.common.process.ProcessInformation;
import systems.reformcloud.reformcloud2.executor.api.common.process.ProcessState;
import systems.reformcloud.reformcloud2.executor.api.common.utility.list.Duo;
import systems.reformcloud.reformcloud2.executor.api.shared.SharedJoinAllowChecker;
import systems.reformcloud.reformcloud2.executor.api.sponge.SpongeExecutor;

import java.util.concurrent.TimeUnit;

public final class PlayerListenerHandler {

    @Listener(order = Order.LATE)
    public void handle(final @NotNull ClientConnectionEvent.Login event) {
        PacketSender sender = DefaultChannelManager.INSTANCE.get("Controller").orElse(null);
        if (sender == null) {
            event.setCancelled(true);
            event.setMessage(Text.of(SpongeExecutor.getInstance().getMessages().format(
                    SpongeExecutor.getInstance().getMessages().getProcessNotReadyToAcceptPlayersMessage()
            )));
            return;
        }

        Duo<Boolean, String> checked = SharedJoinAllowChecker.checkIfConnectAllowed(
                event.getTargetUser()::hasPermission,
                SpongeExecutor.getInstance().getMessages(),
                null,
                event.getTargetUser().getUniqueId(),
                event.getTargetUser().getName()
        );
        if (!checked.getFirst() && checked.getSecond() != null) {
            event.setCancelled(true);
            event.setMessage(Text.of(checked.getSecond()));
        }
    }

    @Listener(order = Order.FIRST)
    public void handle(final @NotNull ClientConnectionEvent.Disconnect event) {
        ProcessInformation current = API.getInstance().getCurrentProcessInformation();
        if (!current.getProcessPlayerManager().isPlayerOnlineOnCurrentProcess(event.getTargetEntity().getUniqueId())) {
            return;
        }

        Sponge.getScheduler()
                .createSyncExecutor(SpongeExecutor.getInstance().getPlugin())
                .schedule(() -> {
                    if (Sponge.getServer().getOnlinePlayers().size() < current.getProcessDetail().getMaxPlayers()
                            && !current.getProcessDetail().getProcessState().equals(ProcessState.READY)
                            && !current.getProcessDetail().getProcessState().equals(ProcessState.INVISIBLE)) {
                        current.getProcessDetail().setProcessState(ProcessState.READY);
                    }

                    current.updateRuntimeInformation();
                    current.getProcessPlayerManager().onLogout(event.getTargetEntity().getUniqueId());
                    SpongeExecutor.getInstance().setThisProcessInformation(current);
                    ExecutorAPI.getInstance().getSyncAPI().getProcessSyncAPI().update(current);
                }, 20, TimeUnit.MILLISECONDS);
    }
}
