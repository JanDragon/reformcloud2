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
package systems.reformcloud.reformcloud2.executor.api.common.commands.permission;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.common.utility.name.Nameable;

import java.util.Collection;

public interface PermissionHolder extends Nameable {

    /**
     * Checks if the user has the specified permission
     *
     * @param permission The permission which should be checked
     * @return If the user has the permission
     */
    boolean hasPermission(@NotNull String permission);

    /**
     * Checks if the given permission value is set
     *
     * @param permission The permission which should be checked
     * @return If the permission is set
     */
    boolean isPermissionSet(@NotNull String permission);

    /**
     * Checks if the user has the specified permission
     *
     * @param permission Checks if the user has the given {@link Permission}
     * @return If the user has the permission else {@link Permission#defaultResult()}
     */
    boolean hasPermission(@NotNull Permission permission);

    /**
     * Checks if the user has the specified permission
     *
     * @param permission Checks if the user has the given {@link Permission}
     * @return If the user has the permission else {@link Permission#defaultResult()}
     */
    boolean isPermissionSet(@NotNull Permission permission);

    /**
     * @return All permissions of the user
     */
    @NotNull
    Collection<Permission> getEffectivePermissions();

    /**
     * Recalculates the permission of the user
     */
    void recalculatePermissions();

    /**
     * @return The permission check for the current user
     */
    @NotNull
    PermissionCheck check();
}
