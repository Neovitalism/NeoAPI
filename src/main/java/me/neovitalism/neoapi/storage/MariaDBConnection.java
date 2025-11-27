package me.neovitalism.neoapi.storage;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import me.neovitalism.neoapi.config.Configuration;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/*
| SQL Data Type   | Java Type       |
|-----------------|-----------------|
| CHAR(n)         | String          |
| VARCHAR(n)      | String          |
| TEXT            | String          |
| INT             | int             |
| BIGINT          | long            |
| FLOAT           | float           |
| DOUBLE          | double          |
| BOOLEAN         | boolean         |
| JSON            | JSONObject      |
| UUID = CHAR(36) | UUID.toString() |
*/
public class MariaDBConnection {
    private final HikariDataSource dataSource;

    public MariaDBConnection(Configuration settings) {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:mariadb://" + settings.getString("address") + "/" + settings.getString("database"));
        config.setUsername(settings.getString("username"));
        config.setPassword(settings.getString("password"));
        config.setMaximumPoolSize(10);
        config.setMinimumIdle(2);
        config.setIdleTimeout(15000);
        config.setMaxLifetime(30000);
        config.setLeakDetectionThreshold(5000);
        this.dataSource = new HikariDataSource(config);
    }

    public boolean testConnection() {
        try (Connection connection = this.dataSource.getConnection()) {
            return connection.isValid(2);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void createTable(String table, String arguments) {
        this.query("CREATE TABLE IF NOT EXISTS " + table + " (" + arguments + ");");
    }

    public void query(String statement) {
        try (Connection connection = this.dataSource.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(statement);
            preparedStatement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void query(String statement, Query query) {
        try (Connection connection = this.dataSource.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(statement);
            query.accept(preparedStatement);
            preparedStatement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void queryBatch(String statement, Query query) {
        try (Connection connection = this.dataSource.getConnection()) {
            connection.setAutoCommit(false);
            PreparedStatement preparedStatement = connection.prepareStatement(statement);
            query.accept(preparedStatement);
            preparedStatement.executeBatch();
            connection.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public <T> T query(String statement, Query query, ResultHandler<T> resultHandler) {
        try (Connection connection = this.dataSource.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(statement);
            query.accept(preparedStatement);
            return resultHandler.apply(preparedStatement.executeQuery());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @FunctionalInterface
    public interface Query {
        void accept(PreparedStatement statement) throws SQLException;
    }

    @FunctionalInterface
    public interface ResultHandler<T> {
        T apply(ResultSet resultSet) throws SQLException;
    }

    public void shutdown() {
        this.dataSource.close();
    }
}
