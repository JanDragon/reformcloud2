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
package systems.reformcloud.reformcloud2.executor.api.common.api.console;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public interface ConsoleSyncAPI {

    /**
     * Dispatches a command into the console and waits for a result
     *
     * @param commandLine The command line which should be executed
     * @return The result of the command or {@code null} if the command is
     * a) not registered
     * b) doesn't sent any result to the handler
     * @deprecated Use {@link #dispatchConsoleCommandAndGetResult(String)} instead
     */
    @Nullable
    @Deprecated
    @ApiStatus.ScheduledForRemoval(inVersion = "2.5")
    String dispatchCommandAndGetResult(@NotNull String commandLine);

    /**
     * Executes a command in the controller/node console
     *
     * @param commandLine The command line which should be executed
     * @return The full result of messages sent by the console command
     */
    @NotNull
    Collection<String> dispatchConsoleCommandAndGetResult(@NotNull String commandLine);
}
