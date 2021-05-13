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
package fr.neutronstars.database.api;

import java.util.List;

public interface Query<T>
{
    Query<T> insertInto(List<String> columns, String... values);

    Query<T> insertInto(List<String> columns, List<String>... values);
    Query<T> insertInto(String[] columns, List<String>... values);
    Query<T> insertInto(String[] columns, String[]... values);
    Query<T> insertInto(String[] columns, String... values);

    Query<T> onDuplicateKey(String... values);

    Query<T> select(String... columns);

    Query<T> update(String... values);

    Query<T> delete();

    Query<T> join(String table, String condition);

    Query<T> join(Query<?> query, String alias, String condition);

    Query<T> join(String table, String alias, String condition);

    Query<T> join(Join join, String table, String condition);

    Query<T> join(Join join, Query<?> query, String alias, String condition);

    Query<T> join(Join join, String table, String alias, String condition);

    Query<T> where(String closeWhere);

    Query<T> where(String closeWhere, Where where);

    Query<T> where(String keyWhere, WhereCondition whereCondition, String valueWhere);

    Query<T> where(String keyWhere, String whereCondition, String valueWhere);

    Query<T> where(String keyWhere, WhereCondition whereCondition, Query<?> valueQuery);

    Query<T> where(String keyWhere, String whereCondition, Query<?> valueQuery);

    Query<T> where(Where where, String keyWhere, WhereCondition whereCondition, String valueWhere);

    Query<T> where(Where where, String keyWhere, String whereCondition, String valueWhere);

    Query<T> where(Where where, String keyWhere, String whereCondition, Query<?> valueQuery);

    Query<T> where(Where where, String keyWhere, WhereCondition whereCondition, Query<?> valueQuery);

    Query<T> groupBy(String column);

    Query<T> having(String closeWhere);

    Query<T> having(String closeWhere, Where where);

    Query<T> having(String keyWhere, WhereCondition whereCondition, String valueWhere);

    Query<T> having(String keyWhere, String whereCondition, String valueWhere);

    Query<T> having(String keyWhere, WhereCondition whereCondition, Query<?> valueQuery);

    Query<T> having(String keyWhere, String whereCondition, Query<?> valueQuery);

    Query<T> having(Where where, String keyWhere, WhereCondition whereCondition, String valueWhere);

    Query<T> having(Where where, String keyWhere, String whereCondition, String valueWhere);

    Query<T> having(Where where, String keyWhere, String whereCondition, Query<?> valueQuery);

    Query<T> having(Where where, String keyWhere, WhereCondition whereCondition, Query<?> valueQuery);

    Query<T> orderBy(String column, Order order);

    Query<T> limit(int limit);

    Query<T> limit(int limit, int offset);

    Query<T> union(String table);

    Query<T> union(String table, boolean all);

    <R> R getResult(Object... params) throws Exception;

    void execute(Object... params) throws Exception;

    T build();

    enum Where
    {
        AND, OR
    }

    enum WhereCondition
    {
        IN, NOT_IN, IS, IS_NOT, LIKE;

        @Override
        public String toString()
        {
            return super.toString().replace("_", " ");
        }
    }

    enum Order
    {
        ASC, DESC
    }

    enum Join
    {
        LEFT,
        RIGHT,
        INNER,
        FULL,
        CROSS,
        SELF,
        NATURAL;

        @Override
        public String toString() {
            return super.toString()+" JOIN";
        }
    }
}
