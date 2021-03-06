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
package systems.reformcloud.reformcloud2.executor.api.common.restapi.request.defaults;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.common.restapi.auth.Auth;
import systems.reformcloud.reformcloud2.executor.api.common.restapi.request.RequestHandler;
import systems.reformcloud.reformcloud2.executor.api.common.restapi.request.RequestListenerHandler;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public final class DefaultRequestListenerHandler implements RequestListenerHandler {

    private final List<RequestHandler> requestHandlers = new ArrayList<>();
    private Auth auth;

    public DefaultRequestListenerHandler(Auth auth) {
        this.auth = auth;
    }

    @Override
    public void setAuth(@NotNull Auth auth) {
        this.auth = auth;
    }

    @NotNull
    @Override
    public Auth authHandler() {
        return this.auth;
    }

    @NotNull
    @Override
    public RequestListenerHandler registerListener(@NotNull RequestHandler requestHandler) {
        this.requestHandlers.add(requestHandler);
        return this;
    }

    @NotNull
    @Override
    public RequestListenerHandler registerListener(@NotNull Class<? extends RequestHandler> requestHandler) {
        try {
            return this.registerListener(requestHandler.getDeclaredConstructor().newInstance());
        } catch (final IllegalAccessException | NoSuchMethodException | InvocationTargetException | InstantiationException ex) {
            ex.printStackTrace();
        }
        return this;
    }

    @Override
    public void unregisterHandler(@NotNull RequestHandler requestHandler) {
        this.requestHandlers.remove(requestHandler);
    }

    @NotNull
    @Override
    public Collection<RequestHandler> getHandlers() {
        return Collections.unmodifiableList(this.requestHandlers);
    }
}
