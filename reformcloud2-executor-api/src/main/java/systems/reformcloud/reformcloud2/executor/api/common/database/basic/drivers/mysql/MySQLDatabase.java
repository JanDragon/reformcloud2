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
package systems.reformcloud.reformcloud2.executor.api.common.database.basic.drivers.mysql;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.common.database.Database;
import systems.reformcloud.reformcloud2.executor.api.common.database.DatabaseReader;
import systems.reformcloud.reformcloud2.executor.api.common.database.sql.SQLDatabaseReader;
import systems.reformcloud.reformcloud2.executor.api.common.utility.maps.AbsentMap;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Map;

public final class MySQLDatabase extends Database<Connection> {

    private static final String CONNECT_ARGUMENTS = "jdbc:mysql://%s:%d/%s?serverTimezone=UTC";

    static {
        MySQLDatabaseDependencyLoader.load(DEPENDENCY_LOADER);
    }

    private final Map<String, DatabaseReader> perTableReader = new AbsentMap<>();

    private HikariDataSource hikariDataSource;

    @Override
    public void connect(@NotNull String host, int port, @NotNull String userName, @NotNull String password, @NotNull String table) {
        if (this.isConnected()) {
            return;
        }

        HikariConfig hikariConfig = new HikariConfig();

        hikariConfig.setJdbcUrl(String.format(CONNECT_ARGUMENTS, host, port, table));
        hikariConfig.setDriverClassName("com.mysql.cj.jdbc.Driver");
        hikariConfig.setUsername(userName);
        hikariConfig.setPassword(password);

        hikariConfig.addDataSourceProperty("cachePrepStmts", "true");
        hikariConfig.addDataSourceProperty("prepStmtCacheSize", "250");
        hikariConfig.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        hikariConfig.addDataSourceProperty("useServerPrepStmts", "true");
        hikariConfig.addDataSourceProperty("useLocalSessionState", "true");
        hikariConfig.addDataSourceProperty("rewriteBatchedStatements", "true");
        hikariConfig.addDataSourceProperty("cacheResultSetMetadata", "true");
        hikariConfig.addDataSourceProperty("cacheServerConfiguration", "true");
        hikariConfig.addDataSourceProperty("elideSetAutoCommits", "true");
        hikariConfig.addDataSourceProperty("maintainTimeStats", "false");

        this.hikariDataSource = new HikariDataSource(hikariConfig);
    }

    @Override
    public boolean isConnected() {
        return this.hikariDataSource != null && !this.hikariDataSource.isClosed();
    }

    @Override
    public void disconnect() {
        if (this.isConnected()) {
            this.hikariDataSource.close();
            this.hikariDataSource = null;
        }
    }

    @Override
    public boolean createDatabase(String name) {
        try (Connection connection = this.get();
             PreparedStatement statement = SQLDatabaseReader.prepareStatement("CREATE TABLE IF NOT EXISTS `" + name + "` (`key` TEXT, `identifier` TEXT, `data` LONGBLOB);", connection)) {
            statement.executeUpdate();
            return true;
        } catch (final SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean deleteDatabase(String name) {
        try (Connection connection = this.get();
             PreparedStatement statement = SQLDatabaseReader.prepareStatement("DROP TABLE `" + name + "`", connection)) {
            statement.executeUpdate();
            return true;
        } catch (final SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    @Override
    public DatabaseReader createForTable(String table) {
        this.createDatabase(table);
        return this.perTableReader.putIfAbsent(table, new SQLDatabaseReader(table, this));
    }

    @NotNull
    @Override
    public Connection get() {
        try {
            return this.hikariDataSource.getConnection();
        } catch (final SQLException ex) {
            throw new RuntimeException(ex);
        }
    }
}
