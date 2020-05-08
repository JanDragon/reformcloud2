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
package systems.reformcloud.reformcloud2.executor.api.common.application;

import com.google.gson.reflect.TypeToken;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import systems.reformcloud.reformcloud2.executor.api.common.ExecutorAPI;
import systems.reformcloud.reformcloud2.executor.api.common.application.basic.DefaultLoadedApplication;
import systems.reformcloud.reformcloud2.executor.api.common.utility.name.Nameable;

/**
 * This class represents an application which is loaded
 *
 * @see ApplicationLoader#getApplication(String)
 */
public interface LoadedApplication extends Nameable {

    TypeToken<DefaultLoadedApplication> TYPE = new TypeToken<DefaultLoadedApplication>() {
    };

    /**
     * @return The application loader which has loaded the application
     */
    @NotNull
    ApplicationLoader loader();

    /**
     * @return The current instance of the {@link ExecutorAPI}
     */
    @NotNull
    ExecutorAPI api();

    /**
     * @return The provided config of the application
     */
    @NotNull
    ApplicationConfig applicationConfig();

    /**
     * @return The current lifecycle status of the application
     */
    @NotNull
    ApplicationStatus applicationStatus();

    /**
     * @return The main class of the application
     */
    @Nullable
    Class<?> mainClass();

    /**
     * Updates the application status
     *
     * @param status The new status of the application
     */
    void setApplicationStatus(@NotNull ApplicationStatus status);

    /**
     * @return The name of the application
     * @see #applicationConfig()
     */
    @NotNull
    @Override
    default String getName() {
        return applicationConfig().getName();
    }
}
