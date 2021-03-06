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
package systems.reformcloud.reformcloud2.executor.client.config;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

public final class ClientConfig {

    public static final Path PATH = Paths.get("reformcloud/config.json");
    private final int maxMemory;
    private final int maxProcesses;
    private final double maxCpu;
    private final String startHost;
    private final String name;
    private final UUID uniqueID;

    public ClientConfig(int maxMemory, int maxProcesses, double maxCpu, String startHost) {
        this.maxMemory = maxMemory;
        this.maxProcesses = maxProcesses;
        this.maxCpu = maxCpu;
        this.startHost = startHost;
        this.name = "Client-" + UUID.randomUUID().toString().split("-")[0];
        this.uniqueID = UUID.randomUUID();
    }

    public int getMaxMemory() {
        return this.maxMemory;
    }

    public int getMaxProcesses() {
        return this.maxProcesses;
    }

    public double getMaxCpu() {
        return this.maxCpu;
    }

    public String getStartHost() {
        return this.startHost;
    }

    public String getName() {
        return this.name;
    }

    public UUID getUniqueID() {
        return this.uniqueID;
    }
}
