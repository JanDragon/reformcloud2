package systems.reformcloud.reformcloud2.executor.api.common.network.packet;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import systems.reformcloud.reformcloud2.executor.api.common.configuration.JsonConfiguration;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.UUID;

public class JsonPacket implements Packet {

    public JsonPacket(int id, @NotNull JsonConfiguration content) {
        this(id, content, null);
    }

    public JsonPacket(int id, @NotNull JsonConfiguration content, @Nullable UUID queryUniqueID) {
        this(id, content, queryUniqueID, new byte[]{0});
    }

    public JsonPacket(int id, @NotNull JsonConfiguration configuration, @Nullable UUID queryUniqueID, @NotNull byte[] extra) {
        this.id = id;
        this.uid = queryUniqueID;
        this.content = configuration;
        this.extra = extra;
    }

    private final int id;

    private UUID uid;

    private JsonConfiguration content;

    private final byte[] extra;

    @Override
    public int packetID() {
        return id;
    }

    @Override
    public UUID queryUniqueID() {
        return uid;
    }

    @NotNull
    @Override
    public JsonConfiguration content() {
        return content;
    }

    @NotNull
    @Override
    public byte[] extra() {
        return extra;
    }

    @Override
    public void setQueryID(UUID id) {
        this.uid = id;
    }

    @Override
    public void setContent(JsonConfiguration content) {
        this.content = content;
    }

    @Override
    public void write(@NotNull ObjectOutputStream objectOutputStream) throws IOException {
        objectOutputStream.writeUTF(uid == null ? "null" : uid.toString());
        objectOutputStream.writeObject(content.toPrettyBytes());
        objectOutputStream.writeObject(extra.length == 0 ? new byte[]{0} : extra);
    }
}
