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
package systems.reformcloud.reformcloud2.executor.controller.api.database;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import systems.reformcloud.reformcloud2.executor.api.common.api.database.DatabaseAsyncAPI;
import systems.reformcloud.reformcloud2.executor.api.common.api.database.DatabaseSyncAPI;
import systems.reformcloud.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.database.Database;
import systems.reformcloud.reformcloud2.executor.api.common.database.DatabaseReader;
import systems.reformcloud.reformcloud2.executor.api.common.utility.list.Streams;
import systems.reformcloud.reformcloud2.executor.api.common.utility.task.Task;
import systems.reformcloud.reformcloud2.executor.api.common.utility.task.defaults.DefaultTask;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

public class DatabaseAPIImplementation implements DatabaseAsyncAPI, DatabaseSyncAPI {

    private final Database<?> database;

    public DatabaseAPIImplementation(Database<?> parent) {
        this.database = parent;
    }

    @NotNull
    @Override
    public Task<JsonConfiguration> findAsync(@NotNull String table, @NotNull String key, String identifier) {
        Task<JsonConfiguration> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> {
            DatabaseReader databaseReader = this.database.createForTable(table);
            if (databaseReader == null) {
                task.complete(null);
                return;
            }

            JsonConfiguration result = databaseReader.find(key).getUninterruptedly();
            if (result != null) {
                task.complete(result);
            } else if (identifier != null) {
                task.complete(databaseReader.findIfAbsent(identifier).getUninterruptedly());
            } else {
                task.complete(null);
            }
        });
        return task;
    }

    @NotNull
    @Override
    public <T> Task<T> findAsync(@NotNull String table, @NotNull String key, String identifier, @NotNull Function<JsonConfiguration, T> function) {
        Task<T> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> {
            JsonConfiguration jsonConfiguration = this.findAsync(table, key, identifier).getUninterruptedly();
            if (jsonConfiguration != null) {
                task.complete(function.apply(jsonConfiguration));
            } else {
                task.complete(null);
            }
        });
        return task;
    }

    @NotNull
    @Override
    public Task<Collection<JsonConfiguration>> getCompleteDatabaseAsync(@NotNull String table) {
        return Task.supply(() -> {
            DatabaseReader reader = this.database.createForTable(table);
            return reader == null ? new ArrayList<>() : Streams.fromIterator(reader.iterator());
        });
    }

    @NotNull
    @Override
    public <T> Task<Collection<T>> getCompleteDatabaseAsync(@NotNull String table, @NotNull Function<JsonConfiguration, T> mapper) {
        return Task.supply(() -> this.getCompleteDatabase(table).stream().map(mapper).filter(Objects::nonNull).collect(Collectors.toList()));
    }

    @NotNull
    @Override
    public Task<Void> insertAsync(@NotNull String table, @NotNull String key, String identifier, @NotNull JsonConfiguration data) {
        Task<Void> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> {
            DatabaseReader databaseReader = this.database.createForTable(table);
            if (databaseReader == null) {
                task.complete(null);
                return;
            }

            databaseReader.insert(key, identifier, data).awaitUninterruptedly();
            task.complete(null);
        });
        return task;
    }

    @NotNull
    @Override
    public Task<Boolean> updateAsync(@NotNull String table, @NotNull String key, @NotNull JsonConfiguration newData) {
        DatabaseReader databaseReader = this.database.createForTable(table);
        if (databaseReader == null) {
            return Task.completedTask(false);
        }

        return databaseReader.update(key, newData);
    }

    @NotNull
    @Override
    public Task<Boolean> updateIfAbsentAsync(@NotNull String table, @NotNull String identifier, @NotNull JsonConfiguration newData) {
        DatabaseReader databaseReader = this.database.createForTable(table);
        if (databaseReader == null) {
            return Task.completedTask(null);
        }

        return databaseReader.updateIfAbsent(identifier, newData);
    }

    @NotNull
    @Override
    public Task<Void> updateAsync(@NotNull String table, @Nullable String key, @Nullable String identifier, @NotNull JsonConfiguration newData) {
        return Task.supply(() -> {
            DatabaseReader reader = this.database.createForTable(table);
            if (reader == null) {
                return null;
            }

            if (key != null) {
                Boolean success = reader.update(key, newData).getUninterruptedly(TimeUnit.SECONDS, 5);
                if (success != null && success) {
                    return null;
                }
            }

            if (identifier != null) {
                reader.updateIfAbsent(identifier, newData);
            }

            return null;
        });
    }

    @NotNull
    @Override
    public Task<Void> removeAsync(@NotNull String table, @NotNull String key) {
        DatabaseReader databaseReader = this.database.createForTable(table);
        if (databaseReader == null) {
            return Task.completedTask(null);
        }

        return databaseReader.remove(key);
    }

    @NotNull
    @Override
    public Task<Void> removeIfAbsentAsync(@NotNull String table, @NotNull String identifier) {
        DatabaseReader databaseReader = this.database.createForTable(table);
        if (databaseReader == null) {
            return Task.completedTask(null);
        }

        return databaseReader.removeIfAbsent(identifier);
    }

    @NotNull
    @Override
    public Task<Void> removeAsync(@NotNull String table, @Nullable String key, @Nullable String identifier) {
        return Task.supply(() -> {
            DatabaseReader reader = this.database.createForTable(table);
            if (reader == null) {
                return null;
            }

            if (key != null) {
                reader.remove(key);
            }

            if (identifier != null) {
                reader.removeIfAbsent(identifier);
            }

            return null;
        });
    }

    @NotNull
    @Override
    public Task<Boolean> createDatabaseAsync(@NotNull String name) {
        Task<Boolean> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> task.complete(this.database.createDatabase(name)));
        return task;
    }

    @NotNull
    @Override
    public Task<Boolean> deleteDatabaseAsync(@NotNull String name) {
        Task<Boolean> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> task.complete(this.database.deleteDatabase(name)));
        return task;
    }

    @NotNull
    @Override
    public Task<Boolean> containsAsync(@NotNull String table, @NotNull String key) {
        DatabaseReader databaseReader = this.database.createForTable(table);
        if (databaseReader == null) {
            return Task.completedTask(null);
        }

        return databaseReader.contains(key);
    }

    @NotNull
    @Override
    public Task<Integer> sizeAsync(@NotNull String table) {
        DatabaseReader databaseReader = this.database.createForTable(table);
        if (databaseReader == null) {
            return Task.completedTask(null);
        }

        return databaseReader.size();
    }

    @Override
    public JsonConfiguration find(@NotNull String table, @NotNull String key, String identifier) {
        return this.findAsync(table, key, identifier).getUninterruptedly();
    }

    @Override
    public <T> T find(@NotNull String table, @NotNull String key, String identifier, @NotNull Function<JsonConfiguration, T> function) {
        return this.findAsync(table, key, identifier, function).getUninterruptedly();
    }

    @NotNull
    @Override
    public Collection<JsonConfiguration> getCompleteDatabase(@NotNull String table) {
        Collection<JsonConfiguration> result = this.getCompleteDatabaseAsync(table).getUninterruptedly();
        return result == null ? new ArrayList<>() : result;
    }

    @NotNull
    @Override
    public <T> Collection<T> getCompleteDatabase(@NotNull String table, @NotNull Function<JsonConfiguration, T> mapper) {
        Collection<T> result = this.getCompleteDatabaseAsync(table, mapper).getUninterruptedly();
        return result == null ? new ArrayList<>() : result;
    }

    @Override
    public void insert(@NotNull String table, @NotNull String key, String identifier, @NotNull JsonConfiguration data) {
        this.insertAsync(table, key, identifier, data).awaitUninterruptedly();
    }

    @Override
    public boolean update(@NotNull String table, @NotNull String key, @NotNull JsonConfiguration newData) {
        Boolean result = this.updateAsync(table, key, newData).getUninterruptedly();
        return result == null ? false : result;
    }

    @Override
    public boolean updateIfAbsent(@NotNull String table, @NotNull String identifier, @NotNull JsonConfiguration newData) {
        Boolean result = this.updateIfAbsentAsync(table, identifier, newData).getUninterruptedly();
        return result == null ? false : result;
    }

    @Override
    public void update(@NotNull String table, @Nullable String key, @Nullable String identifier, @NotNull JsonConfiguration newData) {
        this.updateAsync(table, key, identifier, newData).awaitUninterruptedly();
    }

    @Override
    public void remove(@NotNull String table, @NotNull String key) {
        this.removeAsync(table, key).awaitUninterruptedly();
    }

    @Override
    public void removeIfAbsent(@NotNull String table, @NotNull String identifier) {
        this.removeIfAbsentAsync(table, identifier).awaitUninterruptedly();
    }

    @Override
    public void remove(@NotNull String table, @Nullable String key, @Nullable String identifier) {
        this.removeAsync(table, key, identifier).awaitUninterruptedly();
    }

    @Override
    public boolean createDatabase(@NotNull String name) {
        Boolean result = this.createDatabaseAsync(name).getUninterruptedly();
        return result == null ? false : result;
    }

    @Override
    public boolean deleteDatabase(@NotNull String name) {
        Boolean result = this.deleteDatabaseAsync(name).getUninterruptedly();
        return result == null ? false : result;
    }

    @Override
    public boolean contains(@NotNull String table, @NotNull String key) {
        Boolean result = this.containsAsync(table, key).getUninterruptedly();
        return result == null ? false : result;
    }

    @Override
    public int size(@NotNull String table) {
        Integer result = this.sizeAsync(table).getUninterruptedly();
        return result == null ? 0 : result;
    }
}
