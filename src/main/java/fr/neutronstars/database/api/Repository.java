package fr.neutronstars.database.api;

import fr.neutronstars.database.api.annotation.Table;

import java.sql.ResultSet;
import java.util.List;
import java.util.Optional;

public interface Repository<T>
{
    Optional<T> find(Object identifier) throws Exception;

    Optional<T> find(String column, Object identifier) throws Exception;

    List<T> findAll() throws Exception;

    Query<ResultSet> createQuery() throws Exception;

    Query<ResultSet> createQuery(String alias) throws Exception;
}
