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
package systems.reformcloud.reformcloud2.executor.api.common.network.channel;

import io.netty.channel.ChannelHandlerContext;
import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.common.network.challenge.ChallengeAuthHandler;
import systems.reformcloud.reformcloud2.executor.api.common.network.handler.ChannelReaderHelper;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.Packet;

import java.io.IOException;

public interface NetworkChannelReader {

    /**
     * @return The current packet sender of the reader
     */
    @NotNull
    PacketSender sender();

    /**
     * Sets the context of the channel and the name if the current channel.
     * <br>
     * This method can only get called once. If you call it twice it will throws an exception.
     *
     * @param channelHandlerContext The context of the channel which should get registered
     * @param name                  The name of the channel which should get registered
     * @see systems.reformcloud.reformcloud2.executor.api.common.network.handler.ChannelReaderHelper Gets called there
     */
    void setChannelHandlerContext(@NotNull ChannelHandlerContext channelHandlerContext, @NotNull String name);

    /**
     * Gets called when a channel opens
     *
     * @param context The channel context of the channel
     */
    void channelActive(@NotNull ChannelHandlerContext context);

    /**
     * Gets called when a channel closes
     *
     * @param context The channel context
     */
    void channelInactive(@NotNull ChannelHandlerContext context);

    /**
     * Gets called when a packet comes into the channel
     *
     * @param context     The context of the channel where the packet is from
     * @param authHandler The handler with did the auth of the channel
     * @param parent      The handle which read the packet from the channel
     * @param input       The sent content by the sender
     */
    void read(@NotNull ChannelHandlerContext context, @NotNull ChallengeAuthHandler authHandler,
              @NotNull ChannelReaderHelper parent, @NotNull Packet input);

    /**
     * Handles the exceptions which will occur in the channel
     *
     * @param context The context of the channel the exception occurred in
     * @param cause   The cause why the exception occurred
     */
    default void exceptionCaught(@NotNull ChannelHandlerContext context, @NotNull Throwable cause) {
        if (!(cause instanceof IOException)) {
            System.err.println("Exception in channel " + context.channel().remoteAddress());
            cause.printStackTrace();
        }
    }

    /**
     * Gets called after the channel read
     *
     * @param context The context of the channel
     */
    default void readOperationCompleted(@NotNull ChannelHandlerContext context) {
        context.flush();
    }
}
