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
package systems.reformcloud.reformcloud2.executor.node.process.startup;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.common.language.LanguageManager;
import systems.reformcloud.reformcloud2.executor.api.common.process.ProcessInformation;
import systems.reformcloud.reformcloud2.executor.api.common.process.running.RunningProcess;
import systems.reformcloud.reformcloud2.executor.node.NodeExecutor;
import systems.reformcloud.reformcloud2.executor.node.process.basic.BasicLocalNodeProcess;

import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

public class LocalProcessQueue implements Runnable {

    private static final BlockingDeque<RunningProcess> QUEUE = new LinkedBlockingDeque<>();

    public static void queue(@NotNull ProcessInformation processInformation) {
        RunningProcess localNodeProcess = new BasicLocalNodeProcess(processInformation);
        int size = QUEUE.size();
        System.out.println(LanguageManager.get("client-process-now-in-queue", processInformation.getProcessDetail().getName(), size + 1));

        localNodeProcess.prepare().onComplete(e -> {
            localNodeProcess.handleEnqueue();
            QUEUE.offerLast(localNodeProcess);
        });
    }

    public static void queue(@NotNull RunningProcess process) {
        System.out.println(LanguageManager.get("client-process-now-in-queue",
                process.getProcessInformation().getProcessDetail().getName(), QUEUE.size() + 1));
        process.handleEnqueue();
        QUEUE.offerLast(process);
    }

    @Override
    public void run() {
        if (!NodeExecutor.getInstance().getClusterSyncManager().isConnectedAndSyncWithCluster()) {
            return;
        }

        RunningProcess process = QUEUE.pollFirst();
        if (process == null) {
            return;
        }

        if (NodeExecutor.getInstance().canStartProcesses(process.getProcessInformation().getProcessDetail().getMaxMemory()) && process.bootstrap()) {
            System.out.println(LanguageManager.get("node-process-start", process.getProcessInformation().getProcessDetail().getName()));
            return;
        }

        QUEUE.offerLast(process);
    }
}
