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
package systems.reformcloud.reformcloud2.executor.node.network.packet.out;

import io.netty.channel.ChannelHandlerContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import systems.reformcloud.reformcloud2.executor.api.common.language.LanguageManager;
import systems.reformcloud.reformcloud2.executor.api.common.network.NetworkUtil;
import systems.reformcloud.reformcloud2.executor.api.common.network.challenge.ChallengeAuthHandler;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.NetworkChannelReader;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.PacketSender;
import systems.reformcloud.reformcloud2.executor.api.common.network.data.ProtocolBuffer;
import systems.reformcloud.reformcloud2.executor.api.common.network.handler.ChannelReaderHelper;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.Packet;

import java.util.UUID;

public class NodePacketOutProcessPrepared extends Packet {

    private String processName;
    private UUID processUniqueID;
    private String template;

    public NodePacketOutProcessPrepared() {
    }

    public NodePacketOutProcessPrepared(String processName, UUID processUniqueID, String template) {
        this.processName = processName;
        this.processUniqueID = processUniqueID;
        this.template = template;
    }

    @Override
    public int getId() {
        return NetworkUtil.NODE_TO_NODE_BUS + 10;
    }

    @Override
    public void handlePacketReceive(@NotNull NetworkChannelReader reader, @NotNull ChallengeAuthHandler authHandler, @NotNull ChannelReaderHelper parent, @Nullable PacketSender sender, @NotNull ChannelHandlerContext channel) {
        if (sender == null) {
            return;
        }

        System.out.println(LanguageManager.get(
                "process-prepared",
                this.processName,
                this.processName,
                this.template,
                sender.getName()
        ));
    }

    @Override
    public void write(@NotNull ProtocolBuffer buffer) {
        buffer.writeString(this.processName);
        buffer.writeUniqueId(this.processUniqueID);
        buffer.writeString(this.template);
    }

    @Override
    public void read(@NotNull ProtocolBuffer buffer) {
        this.processName = buffer.readString();
        this.processUniqueID = buffer.readUniqueId();
        this.template = buffer.readString();
    }
}
