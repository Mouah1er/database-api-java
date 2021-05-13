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

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.List;

public class QueryImpl implements Query<String>
{
    private final Database<Connection, ResultSet> database;
    private final String table;
    private final Query<String> unionQuery;
    private final boolean unionAll;

    private String insertInto;
    private String onDuplicateKey;
    private String select;
    private String update;
    private String delete;
    private String join;
    private String where;
    private String groupBy;
    private String having;
    private String orderBy;
    private String limit;

    public QueryImpl(Database<Connection, ResultSet> database, String table)
    {
        this(database, table, null, false);
    }

    private QueryImpl(Database<Connection, ResultSet> database, String table, Query<String> unionQuery, boolean unionAll)
    {
        this.database = database;
        this.table = table;
        this.unionQuery = unionQuery;
        this.unionAll = unionAll;
    }

    @Override
    public Query<String> insertInto(List<String> columns, List<String>... values) {
        return this.insertInto(columns.toArray(new String[0]), values);
    }

    @Override
    public Query<String> insertInto(String[] columns, List<String>... values) {
        String[][] transform = new String[values.length][];
        for(int i = 0; i < values.length; i++){
            transform[i] = values[i].toArray(new String[0]);
        }
        return this.insertInto(columns, transform);
    }

    @Override
    public Query<String> insertInto(String[] columns, String[]... values) {
        String[] transform = new String[values.length];
        for(int i = 0; i < values.length; i++){
            transform[i] = (i > 0 ? "(" : "") + String.join(",", values[i]) + (i < values.length-1 ? ")" : "");
        }
        return this.insertInto(columns, transform);
    }

    @Override
    public Query<String> insertInto(List<String> columns, String... values) {
        return this.insertInto(columns.toArray(new String[0]), values);
    }

    @Override
    public Query<String> insertInto(String[] columns, String... values) {
        if(this.select != null || this.update != null || this.delete != null || this.insertInto != null){
            throw new IllegalArgumentException("Request already started !");
        }
        this.insertInto = "INSERT INTO " + this.table + " (" + String.join(",", columns) + ") VALUES (" + String.join(",", values) + ")";
        return this;
    }

    @Override
    public Query<String> onDuplicateKey(String... values) {
        if(this.insertInto == null){
            throw new IllegalArgumentException("The INSERT INTO query has not started.");
        }
        if(this.onDuplicateKey != null){
            throw new IllegalArgumentException("The duplicate key is already declared !");
        }
        this.onDuplicateKey = " ON DUPLICATE KEY UPDATE " + String.join(",", values);
        return this;
    }

    @Override
    public Query<String> select(String... columns)
    {
        if(this.select != null || this.update != null || this.delete != null || this.insertInto != null){
            throw new IllegalArgumentException("Request already started !");
        }
        this.select = "SELECT "+String.join(",", columns)+" FROM "+this.table;
        return this;
    }

    @Override
    public Query<String> update(String... values)
    {
        if(this.select != null || this.update != null || this.delete != null || this.insertInto != null){
            throw new IllegalArgumentException("Request already started !");
        }
        this.update = "UPDATE " + this.table + " SET " + String.join(",", values);
        return this;
    }

    @Override
    public Query<String> delete()
    {
        if(this.select != null || this.update != null || this.delete != null || this.insertInto != null){
            throw new IllegalArgumentException("Request already started !");
        }
        this.delete = "DELETE FROM "+this.table;
        return this;
    }

    @Override
    public Query<String> join(String table, String condition) {
        return this.join(Join.LEFT, table, condition);
    }

    @Override
    public Query<String> join(Query<?> query, String alias, String condition) {
        return this.join(Join.LEFT, query, alias, condition);
    }

    @Override
    public Query<String> join(String table, String alias, String condition) {
        return this.join(Join.LEFT, table, alias, condition);
    }

    @Override
    public Query<String> join(Join join, String table, String condition) {
        if(this.join == null){
            this.join = "";
        }
        this.join += " " + join.toString() + " " + table + " ON " + condition;
        return this;
    }

    @Override
    public Query<String> join(Join join, Query<?> query, String alias, String condition) {
        return this.join(join, "("+query.build()+")", alias, condition);
    }

    @Override
    public Query<String> join(Join join, String table, String alias, String condition) {
        return this.join(join, table + " AS " + alias, condition);
    }

    @Override
    public Query<String> where(String closeWhere) {
        return this.where(closeWhere, null);
    }

    @Override
    public Query<String> where(String closeWhere, Where where) {
        if(where == null){
            if(this.where != null){
                throw new IllegalArgumentException("The where close is already open !");
            }
            this.where = " WHERE "+closeWhere;
            return this;
        }
        if(this.where == null){
            throw new IllegalArgumentException("The where close is not open !");
        }
        this.where += " " + where.toString() + " " + closeWhere;
        return this;
    }

    @Override
    public Query<String> where(String keyWhere, String whereCondition, Query<?> valueQuery) {
        return this.where(null, keyWhere, whereCondition, valueQuery);
    }

    @Override
    public Query<String> where(String keyWhere, WhereCondition whereCondition, String valueWhere) {
        return this.where(null, keyWhere, whereCondition, valueWhere);
    }

    @Override
    public Query<String> where(String keyWhere, String whereCondition, String valueWhere) {
        return this.where(null, keyWhere, whereCondition, valueWhere);
    }

    @Override
    public Query<String> where(String keyWhere, WhereCondition whereCondition, Query<?> valueQuery) {
        return this.where(null, keyWhere, whereCondition, valueQuery);
    }

    @Override
    public Query<String> where(Where where, String keyWhere, String whereCondition, Query<?> valueQuery)
    {
        return this.where(where, keyWhere, whereCondition, "(" + valueQuery.build() + ")");
    }

    @Override
    public Query<String> where(Where where, String keyWhere, WhereCondition whereCondition, Query<?> valueQuery)
    {
        return this.where(where, keyWhere, whereCondition.toString(), valueQuery);
    }

    @Override
    public Query<String> where(Where where, String keyWhere, WhereCondition whereCondition, String valueWhere)
    {
        return this.where(where, keyWhere, whereCondition.toString(), valueWhere);
    }

    @Override
    public Query<String> where(Where where, String keyWhere, String whereCondition, String valueWhere)
    {
        if(where == null){
            if(this.where != null){
                throw new IllegalArgumentException("The where close is already open !");
            }
            this.where = " WHERE "+keyWhere+" "+whereCondition+" "+valueWhere;
            return this;
        }
        if(this.where == null){
            throw new IllegalArgumentException("The where close is not open !");
        }
        this.where += " " + where.toString()+" "+keyWhere+" "+whereCondition+" "+valueWhere;
        return this;
    }

    @Override
    public Query<String> groupBy(String column) {
        if(this.groupBy != null){
            throw new IllegalArgumentException("The group by close is already open !");
        }
        this.groupBy = " GROUP BY "+column;
        return this;
    }

    @Override
    public Query<String> having(String closeWhere) {
        return this.where(closeWhere, null);
    }

    @Override
    public Query<String> having(String closeWhere, Where where) {
        if(where == null){
            if(this.having != null){
                throw new IllegalArgumentException("The having close is already open !");
            }
            this.having = " HAVING "+closeWhere;
            return this;
        }
        if(this.having == null){
            throw new IllegalArgumentException("The having close is not open !");
        }
        this.having += " " + where.toString() + " " + closeWhere;
        return this;
    }

    @Override
    public Query<String> having(String keyWhere, String whereCondition, Query<?> valueQuery) {
        return this.where(null, keyWhere, whereCondition, valueQuery);
    }

    @Override
    public Query<String> having(String keyWhere, WhereCondition whereCondition, String valueWhere) {
        return this.where(null, keyWhere, whereCondition, valueWhere);
    }

    @Override
    public Query<String> having(String keyWhere, String whereCondition, String valueWhere) {
        return this.where(null, keyWhere, whereCondition, valueWhere);
    }

    @Override
    public Query<String> having(String keyWhere, WhereCondition whereCondition, Query<?> valueQuery) {
        return this.where(null, keyWhere, whereCondition, valueQuery);
    }

    @Override
    public Query<String> having(Where where, String keyWhere, String whereCondition, Query<?> valueQuery)
    {
        return this.where(where, keyWhere, whereCondition, "(" + valueQuery.build() + ")");
    }

    @Override
    public Query<String> having(Where where, String keyWhere, WhereCondition whereCondition, Query<?> valueQuery)
    {
        return this.where(where, keyWhere, whereCondition.toString(), valueQuery);
    }

    @Override
    public Query<String> having(Where where, String keyWhere, WhereCondition whereCondition, String valueWhere)
    {
        return this.where(where, keyWhere, whereCondition.toString(), valueWhere);
    }

    @Override
    public Query<String> having(Where where, String keyWhere, String whereCondition, String valueWhere)
    {
        if(where == null){
            if(this.having != null){
                throw new IllegalArgumentException("The having close is already open !");
            }
            this.having = " HAVING "+keyWhere+" "+whereCondition+" "+valueWhere;
            return this;
        }
        if(this.having == null){
            throw new IllegalArgumentException("The having close is not open !");
        }
        this.having += " " + where.toString()+" "+keyWhere+" "+whereCondition+" "+valueWhere;
        return this;
    }

    @Override
    public Query<String> orderBy(String column, Order order)
    {
        if(this.orderBy == null){
            this.orderBy = " ORDER BY ";
        }else {
            this.orderBy += ", ";
        }
        this.orderBy += column + " " + order.toString();
        return this;
    }

    @Override
    public Query<String> limit(int limit)
    {
        return this.limit(limit, 0);
    }

    @Override
    public Query<String> limit(int limit, int offset)
    {
        if(this.limit != null){
            throw new IllegalArgumentException("The limit close is already open !");
        }
        this.limit = " LIMIT " + limit + " OFFSET " + offset;
        return this;
    }

    @Override
    public Query<String> union(String table)
    {
        return this.union(table,false);
    }

    @Override
    public Query<String> union(String table, boolean all)
    {
        return new QueryImpl(this.database, table, this, all);
    }

    @Override
    public <R> R getResult(Object... params) throws Exception
    {
        return (R) this.database.getResults(this, params);
    }

    @Override
    public void execute(Object... params) throws Exception
    {
        this.database.execute(this, params);
    }

    @Override
    public String build()
    {
        StringBuilder builder = new StringBuilder();

        if(this.unionQuery != null) {
            builder.append(this.unionQuery)
                   .append(" UNION ")
                   .append(this.unionAll ? "ALL " : "");
        }

        if(this.select != null){
            builder.append(this.select);
        }else if(this.update != null){
            builder.append(this.update);
        }else if(this.delete != null){
            builder.append(this.delete);
        }else if(this.insertInto != null){
            builder.append(this.insertInto);
            if(this.onDuplicateKey != null){
                builder.append(this.onDuplicateKey);
            }
            return builder.toString();
        }

        if(this.join != null){
            builder.append(this.join);
        }
        if(this.where != null){
            builder.append(this.where);
        }
        if(this.groupBy != null){
            builder.append(this.groupBy);
        }
        if(this.having != null){
            builder.append(this.having);
        }
        if(this.orderBy != null){
            builder.append(this.orderBy);
        }
        if(this.limit != null){
            builder.append(this.limit);
        }
        return builder.toString();
    }

    @Override
    public String toString() {
        return this.build();
    }
}
