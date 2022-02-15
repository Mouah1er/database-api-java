/**
 * Copyright 2021 NeutronStars
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package fr.neutronstars.database.core;

import fr.neutronstars.database.api.Database;
import fr.neutronstars.database.api.Query;

import java.sql.*;
import java.util.logging.Logger;

public class DatabaseImpl implements Database<Connection, ResultSet>
{
    private static final Logger LOGGER = Logger.getLogger("DATABASE");

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static Database<Connection, ResultSet> create(String database, String user, String password)
    {
        return DatabaseImpl.create("127.0.0.1", "3306", database, user, password, "utf-8");
    }

    public static Database<Connection, ResultSet> create(String database, String user, String password, String charset)
    {
        return DatabaseImpl.create("127.0.0.1", "3306", database, user, password, charset);
    }

    public static Database<Connection, ResultSet> create(String host, String port, String database, String user, String password)
    {
        return DatabaseImpl.create(host, port, database, user, password, "utf-8");
    }

    public static Database<Connection, ResultSet> create(String host, String port, String database, String user, String password, String charset)
    {
        return new DatabaseImpl(host, port, database, user, password, charset);
    }

    protected final String host, port, database, user, password, charset;
    protected Connection connection;

    // to allow polymorphism
    protected DatabaseImpl(String host, String port, String database, String user, String password, String charset)
    {
        this.host = host;
        this.port = port;
        this.database = database;
        this.user = user;
        this.password = password;
        this.charset = charset;
    }

    @Override
    public Database<Connection, ResultSet> connect() throws SQLException
    {
        this.connection = DriverManager.getConnection(
                "jdbc:mysql://"+this.host+":"+this.port+"/"+this.database+"?useUnicode=true&characterEncoding="
                        +this.charset+"&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false"
                        +"&serverTimezone=UTC",
                this.user,
                this.password
        );
        return this;
    }

    @Override
    public Database<Connection, ResultSet> reconnect() throws SQLException
    {
        this.disconnect();
        return this.connect();
    }

    @Override
    public Database<Connection, ResultSet> disconnect() throws SQLException
    {
        if(this.connection != null)
        {
            this.connection.close();
            this.connection = null;
        }
        return this;
    }

    @Override
    public Logger getLogger()
    {
        return DatabaseImpl.LOGGER;
    }

    @Override
    public Connection get() {
        return this.connection;
    }

    @Override
    public <E> Query<E> query(String table) {
        return this.query(table, null);
    }

    @Override
    public <E> Query<E> query(String table, String alias)
    {
        return (Query<E>) new QueryImpl(this,table + (alias != null ? " AS " + alias : ""));
    }

    @Override
    public <E> Query<E> query(Query<?> query, String alias)
    {
        if(alias == null){
            throw new IllegalArgumentException("Alias is mandatory for nested queries.");
        }
        return this.query("(" + query.toString() + ")", alias);
    }

    public ResultSet getResults(Query<?> query, Object... params) throws SQLException
    {
        return this.getResults(String.valueOf(query), params);
    }

    public ResultSet getResults(String query, Object... params) throws SQLException
    {
        return this.getResults(query, true, params);
    }

    private ResultSet getResults(String query, boolean reconnect, Object... params) throws SQLException
    {
        try {
            PreparedStatement statement = this.connection.prepareStatement(query);
            for (int i = 0; i < params.length; i++) {
                statement.setObject(i+1, params[i]);
            }
            return statement.executeQuery();
        }catch (SQLException sqlException) {
            if (reconnect) {
                this.reconnect();
                return this.getResults(query, false, params);
            }
            throw sqlException;
        }
    }

    public void execute(Query<?> query, Object... params) throws SQLException
    {
        this.execute(String.valueOf(query.build()), params);
    }

    public void execute(String query, Object... params) throws SQLException
    {
        this.execute(query, true, params);
    }

    private void execute(String query, boolean reconnect, Object... params) throws SQLException
    {
        try {
            PreparedStatement statement = this.connection.prepareStatement(query);
            for (int i = 0; i < params.length; i++) {
                statement.setObject(i+1, params[i]);
            }
            statement.executeUpdate();
        }catch (SQLException sqlException) {
            if (reconnect) {
                this.reconnect();
                this.execute(query, false, params);
                return;
            }
            throw sqlException;
        }
    }

    public Database<Connection, ResultSet> close(ResultSet resultSet) throws SQLException
    {
        resultSet.close();
        return this;
    }
}