package dev.oof.punish.util.data;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.Callable;
import java.util.function.Function;

public class SelectQueryCallable<V> implements Callable<V> {

    private final DataSource source;
    private final String query;
    private final Object[] args;
    private final Function<ResultSet, V> function;

    public SelectQueryCallable(DataSource source, String query, Object[] args, Function<ResultSet, V> function) {
        this.source = source;
        this.query = query;
        this.args = args;
        this.function = function;
    }

    @Override
    public V call() throws Exception {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet results = null;

        try {
            // instantiate connection and form statement
            connection = source.getConnection();
            if (connection == null) {
                throw new SQLException("Could not connect to database.");
            }

            statement = connection.prepareStatement(query);
            for (int i = 0; i < args.length; i++) {
                statement.setObject(i + 1, args[i]);
            }

            // apply results to function and return value
            results = statement.executeQuery();
            return function.apply(results);
        }
        catch (Exception e) {
            throw new SQLException(e);
        }
        finally {
            // cleanup connection etc.
            if (connection != null) {
                connection.close();
            }

            if (statement != null) {
                statement.close();
            }

            if (results != null) {
                results.close();
            }
        }
    }
}
