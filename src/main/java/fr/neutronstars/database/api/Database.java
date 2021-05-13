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

import java.sql.ResultSet;
import java.sql.SQLException;

public interface Database<T, R>
{
    T get();

    Database<T, R> connect() throws Exception;

    Database<T, R> reconnect() throws Exception;

    Database<T, R> disconnect() throws Exception;

    <E> Query<E> query(String table);

    <E> Query<E> query(String table, String alias);

    <E> Query<E> query(Query<?> query, String alias);

    R getResults(Query<?> query, Object... params) throws Exception;

    void execute(Query<?> query, Object... params) throws Exception;

    Database<T, R> close(R result) throws Exception;
}
