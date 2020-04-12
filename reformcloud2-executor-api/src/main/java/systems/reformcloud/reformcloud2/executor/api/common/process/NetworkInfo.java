package systems.reformcloud.reformcloud2.executor.api.common.process;

import java.net.InetSocketAddress;

public final class NetworkInfo {

    public NetworkInfo(String host, int port) {
        this.host = host;
        this.port = port;
        this.connectTime = -1L;
    }

    private String host;

    private int port;

    private long connectTime;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public InetSocketAddress toInet() {
        return new InetSocketAddress(getHost(), getPort());
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public boolean isConnected() {
        return connectTime != -1;
    }

    public void setConnected(boolean connected) {
        if (connected) {
            this.connectTime = System.currentTimeMillis();
        } else {
            this.connectTime = -1;
        }
    }

    public long getConnectTime() {
        return connectTime;
    }

    @Override
    public String toString() {
        return host + ":" + port;
    }
}
