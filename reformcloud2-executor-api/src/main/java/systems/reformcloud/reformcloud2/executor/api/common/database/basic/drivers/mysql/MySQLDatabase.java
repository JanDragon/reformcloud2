package systems.reformcloud.reformcloud2.executor.api.common.database.basic.drivers.mysql;

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

    private final Map<String, DatabaseReader> perTableReader = new AbsentMap<>();

    private static final String CONNECT_ARGUMENTS = "jdbc:mysql://%s:%d/%s?serverTimezone=UTC";

    static {
        MySQLDatabaseDependencyLoader.load(DEPENDENCY_LOADER);
    }

    private String host;

    private int port;

    private String userName;

    private String password;

    private String table;

    private HikariDataSource hikariDataSource;

    @Override
    public void connect(@NotNull String host, int port, @NotNull String userName, @NotNull String password, @NotNull String table) {
        if (!isConnected()) {
            this.host = host;
            this.port = port;
            this.userName = userName;
            this.password = password;
            this.table = table;

            this.hikariDataSource = new HikariDataSource();
            this.hikariDataSource.setJdbcUrl(String.format(CONNECT_ARGUMENTS, host, port, table));
            this.hikariDataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");

            this.hikariDataSource.setUsername(userName);
            this.hikariDataSource.setPassword(password);

            this.hikariDataSource.setValidationTimeout(5000);
            this.hikariDataSource.setConnectionTimeout(5000);
            this.hikariDataSource.setMaximumPoolSize(20);

            this.hikariDataSource.validate();
        }
    }

    @Override
    public boolean isConnected() {
        return this.hikariDataSource != null && !this.hikariDataSource.isClosed();
    }

    @Override
    public void reconnect() {
        disconnect();
        connect(host, port, userName, password, table);
    }

    @Override
    public void disconnect() {
        if (isConnected()) {
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
