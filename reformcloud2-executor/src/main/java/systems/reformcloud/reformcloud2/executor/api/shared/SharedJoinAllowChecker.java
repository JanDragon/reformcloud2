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
package systems.reformcloud.reformcloud2.executor.api.shared;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import systems.reformcloud.reformcloud2.executor.api.api.API;
import systems.reformcloud.reformcloud2.executor.api.common.ExecutorAPI;
import systems.reformcloud.reformcloud2.executor.api.common.groups.messages.IngameMessages;
import systems.reformcloud.reformcloud2.executor.api.common.groups.utils.PlayerAccessConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.process.ProcessInformation;
import systems.reformcloud.reformcloud2.executor.api.common.process.ProcessState;
import systems.reformcloud.reformcloud2.executor.api.common.utility.list.Duo;

import java.util.Collection;
import java.util.UUID;
import java.util.function.Function;

public final class SharedJoinAllowChecker {

    private SharedJoinAllowChecker() {
        throw new UnsupportedOperationException();
    }

    @NotNull
    public static Duo<Boolean, String> checkIfConnectAllowed(@NotNull Function<String, Boolean> permissionChecker,
                                                             @NotNull IngameMessages messages,
                                                             @Nullable Collection<ProcessInformation> checkedConnectedServices,
                                                             @NotNull UUID playerUniqueId,
                                                             @NotNull String username) {
        ProcessInformation current = API.getInstance().getCurrentProcessInformation();
        PlayerAccessConfiguration configuration = current.getProcessGroup().getPlayerAccessConfiguration();

        if (checkedConnectedServices != null
                && checkedConnectedServices.stream().anyMatch(e -> e.getProcessPlayerManager().isPlayerOnlineOnCurrentProcess(playerUniqueId))) {
            return new Duo<>(false, messages.format(messages.getAlreadyConnectedToNetwork()));
        }

        if (configuration.isUseCloudPlayerLimit()
                && current.getProcessDetail().getMaxPlayers() < current.getProcessPlayerManager().getOnlineCount() + 1
                && !permissionChecker.apply(configuration.getFullJoinPermission())) {
            return new Duo<>(false, messages.format(messages.getProcessFullMessage()));
        }

        if (configuration.isJoinOnlyPerPermission() && !permissionChecker.apply(configuration.getJoinPermission())) {
            return new Duo<>(false, messages.format(messages.getProcessEnterPermissionNotSet()));
        }

        if (configuration.isMaintenance() && !permissionChecker.apply(configuration.getMaintenanceJoinPermission())) {
            return new Duo<>(false, messages.format(messages.getProcessInMaintenanceMessage()));
        }

        if (!current.getProcessPlayerManager().onLogin(playerUniqueId, username)) {
            return new Duo<>(false, messages.format(messages.getAlreadyConnectedMessage()));
        }

        if (configuration.isUseCloudPlayerLimit()
                && current.getProcessDetail().getProcessState().equals(ProcessState.READY)
                && current.getProcessDetail().getMaxPlayers() <= current.getProcessPlayerManager().getOnlineCount() + 1) {
            current.getProcessDetail().setProcessState(ProcessState.FULL);
        }

        current.updateRuntimeInformation();
        ExecutorAPI.getInstance().getSyncAPI().getProcessSyncAPI().update(current);
        return new Duo<>(true, null);
    }
}
