package systems.reformcloud.reformcloud2.executor.client.config;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

public final class ClientConfig {

    public static final Path PATH = Paths.get("reformcloud/config.json");

    public ClientConfig(int maxMemory, int maxProcesses, double maxCpu, String startHost) {
        this.maxMemory = maxMemory;
        this.maxProcesses = maxProcesses;
        this.maxCpu = maxCpu;
        this.startHost = startHost;
        this.name = "Client-" + UUID.randomUUID().toString().split("-")[0];
        this.uniqueID = UUID.randomUUID();
    }

    private final int maxMemory;

    private final int maxProcesses;

    private final double maxCpu;

    private final String startHost;

    private final String name;

    private final UUID uniqueID;

    public int getMaxMemory() {
        return maxMemory;
    }

    public int getMaxProcesses() {
        return maxProcesses;
    }

    public double getMaxCpu() {
        return maxCpu;
    }

    public String getStartHost() {
        return startHost;
    }

    public String getName() {
        return name;
    }

    public UUID getUniqueID() {
        return uniqueID;
    }
}
