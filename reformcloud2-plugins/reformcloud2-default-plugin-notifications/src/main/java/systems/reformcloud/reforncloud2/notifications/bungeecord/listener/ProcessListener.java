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
package systems.reformcloud.reforncloud2.notifications.bungeecord.listener;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import systems.reformcloud.reformcloud2.executor.api.bungee.BungeeExecutor;
import systems.reformcloud.reformcloud2.executor.api.common.ExecutorAPI;
import systems.reformcloud.reformcloud2.executor.api.common.api.basic.events.ProcessStartedEvent;
import systems.reformcloud.reformcloud2.executor.api.common.api.basic.events.ProcessStoppedEvent;
import systems.reformcloud.reformcloud2.executor.api.common.api.basic.events.ProcessUpdatedEvent;
import systems.reformcloud.reformcloud2.executor.api.common.event.handler.Listener;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.manager.DefaultChannelManager;
import systems.reformcloud.reformcloud2.executor.api.common.process.ProcessInformation;
import systems.reformcloud.reformcloud2.executor.api.common.utility.task.Task;
import systems.reformcloud.reformcloud2.executor.api.common.utility.thread.AbsoluteThread;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class ProcessListener {

    private static final Map<UUID, ProcessInformation> REGISTERED = new ConcurrentHashMap<>();

    public ProcessListener() {
        Task.EXECUTOR.execute(() -> {
            while (DefaultChannelManager.INSTANCE.get("Controller").isEmpty()) {
                AbsoluteThread.sleep(50);
            }

            ExecutorAPI.getInstance()
                    .getSyncAPI()
                    .getProcessSyncAPI()
                    .getAllProcesses()
                    .forEach(e -> REGISTERED.put(e.getProcessDetail().getProcessUniqueID(), e));
        });
    }

    @Listener
    public void handle(final ProcessStartedEvent event) {
        this.publishNotification(
                BungeeExecutor.getInstance().getMessages().getProcessStarted(),
                event.getProcessInformation().getProcessDetail().getName()
        );
    }

    @Listener
    public void handle(final ProcessStoppedEvent event) {
        if (!REGISTERED.containsKey(event.getProcessInformation().getProcessDetail().getProcessUniqueID())) {
            return;
        }

        this.publishNotification(
                BungeeExecutor.getInstance().getMessages().getProcessStopped(),
                event.getProcessInformation().getProcessDetail().getName()
        );
        REGISTERED.remove(event.getProcessInformation().getProcessDetail().getProcessUniqueID());
    }

    @Listener
    public void handle(final ProcessUpdatedEvent event) {
        ProcessInformation old = REGISTERED.put(
                event.getProcessInformation().getProcessDetail().getProcessUniqueID(),
                event.getProcessInformation()
        );
        if (old != null) {
            if (!old.getNetworkInfo().isConnected() && event.getProcessInformation().getNetworkInfo().isConnected()) {
                this.publishNotification(
                        BungeeExecutor.getInstance().getMessages().getProcessConnected(),
                        event.getProcessInformation().getProcessDetail().getName()
                );
            }

            return;
        }

        this.publishNotification(
                BungeeExecutor.getInstance().getMessages().getProcessRegistered(),
                event.getProcessInformation().getProcessDetail().getName()
        );
    }

    private void publishNotification(String message, Object... replacements) {
        String replacedMessage = BungeeExecutor.getInstance().getMessages().format(message, replacements);
        ProxyServer.getInstance().getPlayers()
                .stream()
                .filter(e -> e.hasPermission("reformcloud.notify"))
                .forEach(player -> player.sendMessage(TextComponent.fromLegacyText(replacedMessage)));
    }
}
