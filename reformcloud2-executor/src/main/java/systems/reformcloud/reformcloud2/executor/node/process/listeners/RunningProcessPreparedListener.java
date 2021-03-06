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
package systems.reformcloud.reformcloud2.executor.node.process.listeners;

import systems.reformcloud.reformcloud2.executor.api.common.event.handler.Listener;
import systems.reformcloud.reformcloud2.executor.api.common.process.running.events.RunningProcessPreparedEvent;
import systems.reformcloud.reformcloud2.executor.api.common.process.running.manager.SharedRunningProcessManager;
import systems.reformcloud.reformcloud2.executor.node.NodeExecutor;
import systems.reformcloud.reformcloud2.executor.node.network.packet.out.NodePacketOutProcessPrepared;

public class RunningProcessPreparedListener {

    @Listener
    public void handle(final RunningProcessPreparedEvent event) {
        NodeExecutor.getInstance().getNodeNetworkManager().getCluster().broadCastToCluster(new NodePacketOutProcessPrepared(
                event.getRunningProcess().getProcessInformation().getProcessDetail().getName(),
                event.getRunningProcess().getProcessInformation().getProcessDetail().getProcessUniqueID(),
                event.getRunningProcess().getProcessInformation().getProcessDetail().getTemplate().getName()
        ));

        SharedRunningProcessManager.registerRunningProcess(event.getRunningProcess());

        NodeExecutor.getInstance().getNodeNetworkManager().getNodeProcessHelper().registerLocalProcess(event.getRunningProcess());
        NodeExecutor.getInstance().getNodeNetworkManager().getRegisteredProcesses().removeRandom(
                event.getRunningProcess().getProcessInformation().getProcessGroup().getName()
        );
    }
}
