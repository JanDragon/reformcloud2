package systems.reformcloud.reformcloud2.executor.api.common.process.api;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import systems.reformcloud.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.groups.ProcessGroup;
import systems.reformcloud.reformcloud2.executor.api.common.groups.template.Template;
import systems.reformcloud.reformcloud2.executor.api.common.network.SerializableObject;
import systems.reformcloud.reformcloud2.executor.api.common.network.data.ProtocolBuffer;
import systems.reformcloud.reformcloud2.executor.api.common.process.ProcessState;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.UUID;

public class ProcessConfiguration implements SerializableObject {

    @ApiStatus.Internal
    ProcessConfiguration(ProcessGroup base, UUID uniqueId, String displayName,
                         Integer maxMemory, Integer port, Template template, JsonConfiguration extra,
                         int id, int maxPlayers, Collection<ProcessInclusion> inclusions, @NotNull ProcessState initialState) {
        this.base = base;
        this.uniqueId = uniqueId == null ? UUID.randomUUID() : uniqueId;
        this.displayName = displayName;
        this.maxMemory = maxMemory;
        this.port = port;
        this.template = template;
        this.extra = extra;
        this.id = id;
        this.maxPlayers = maxPlayers;
        this.inclusions = inclusions;
        this.initialState = initialState;
    }

    private ProcessGroup base;

    private UUID uniqueId;

    private String displayName;

    private Integer maxMemory;

    private Integer port;

    private Template template;

    private JsonConfiguration extra;

    private int id;

    private int maxPlayers;

    private Collection<ProcessInclusion> inclusions;

    private ProcessState initialState;

    @NotNull
    public ProcessGroup getBase() {
        return base;
    }

    @NotNull
    public UUID getUniqueId() {
        return uniqueId;
    }

    @Nullable
    public String getDisplayName() {
        return displayName;
    }

    @Nullable
    public Integer getMaxMemory() {
        return maxMemory;
    }

    @Nullable
    public Integer getPort() {
        return port;
    }

    @Nullable
    public Template getTemplate() {
        return template;
    }

    @NotNull
    public JsonConfiguration getExtra() {
        return extra;
    }

    public int getId() {
        return id;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    @NotNull
    public ProcessState getInitialState() {
        return initialState;
    }

    @NotNull
    public Collection<ProcessInclusion> getInclusions() {
        return inclusions;
    }

    @Override
    public void write(@NotNull ProtocolBuffer buffer) {
        buffer.writeObject(this.base);
        buffer.writeUniqueId(this.uniqueId);
        buffer.writeString(this.displayName);
        buffer.writeInteger(this.maxMemory);
        buffer.writeInteger(this.port);
        buffer.writeObject(this.template);
        buffer.writeArray(this.extra.toPrettyBytes());
        buffer.writeVarInt(this.id);
        buffer.writeVarInt(this.maxPlayers);
        buffer.writeObjects(this.inclusions);
        buffer.writeVarInt(this.initialState.ordinal());
    }

    @Override
    public void read(@NotNull ProtocolBuffer buffer) {
        this.base = buffer.readObject(ProcessGroup.class);
        this.uniqueId = buffer.readUniqueId();
        this.displayName = buffer.readString();
        this.maxMemory = buffer.readInteger();
        this.port = buffer.readInteger();
        this.template = buffer.readObject(Template.class);

        try (InputStream inputStream = new ByteArrayInputStream(buffer.readArray())) {
            this.extra = new JsonConfiguration(inputStream);
        } catch (final IOException ex) {
            this.extra = new JsonConfiguration();
            ex.printStackTrace();
        }

        this.id = buffer.readVarInt();
        this.maxPlayers = buffer.readVarInt();
        this.inclusions = buffer.readObjects(ProcessInclusion.class);
        this.initialState = ProcessState.values()[buffer.readVarInt()];
    }
}
