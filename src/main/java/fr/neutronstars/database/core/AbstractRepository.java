package fr.neutronstars.database.core;

import fr.neutronstars.database.api.Database;
import fr.neutronstars.database.api.Query;
import fr.neutronstars.database.api.Repository;
import fr.neutronstars.database.api.annotation.Column;
import fr.neutronstars.database.api.annotation.Table;
import fr.neutronstars.database.api.exception.MissingAnnotationException;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;

public abstract class AbstractRepository<T> implements Repository<T>
{
    private Database<Connection, ResultSet> database;

    protected AbstractRepository(Database<Connection, ResultSet> database)
    {
        this.database = database;
    }

    @Override
    public Optional<T> find(Object identifier) throws Exception
    {
        return this.find("id", identifier);
    }

    @Override
    public Optional<T> find(String column, Object identifier) throws Exception
    {
        return Optional.ofNullable(
            (ResultSet) (this.createQuery()
                .select("*")
                .where(column + "=?")
                .getResult(identifier))
        ).map(resultSet -> {
            T t = null;
            try {
                if (resultSet.next()) {
                    t = this.build(resultSet);
                }
                this.database.close(resultSet);
            } catch (Throwable throwable) {
                this.database.getLogger().log(Level.SEVERE, throwable.getMessage(), throwable);
            }
            return t;
        });
    }

    @Override
    public List<T> findAll() throws Exception
    {
        List<T> list = new ArrayList<>();
        Optional.ofNullable((ResultSet) (this.createQuery().select("*").getResult()))
            .ifPresent(resultSet -> {
                try {
                    while (resultSet.next()) {
                        list.add(this.build(resultSet));
                    }
                    this.database.close(resultSet);
                } catch (Throwable throwable) {
                    this.database.getLogger().log(Level.SEVERE, throwable.getMessage(), throwable);
                }
            });
        return list;
    }

    public void persist(T type) throws Exception
    {
        StringBuilder builder = new StringBuilder();
        List<Object> values = new ArrayList<>();
        if (this.isPersisted(type)) {
            StringBuilder where = new StringBuilder();
            List<Object> whereValues = new ArrayList<>();
            for (Field field : type.getClass().getDeclaredFields()) {
                if (field.isAnnotationPresent(Column.class)) {
                    Column column = field.getAnnotation(Column.class);
                    if (column.key() == Column.Key.PRIMARY) {
                        if (where.length() != 0) {
                            where.append(" AND ");
                        }
                        where.append(
                            Optional.of(column.name())
                                .filter(name -> !name.isEmpty())
                                .orElse(field.getName().toLowerCase())
                        ).append("=?");
                        boolean accessible = field.isAccessible();
                        field.setAccessible(true);
                        whereValues.add(field.get(type));
                        field.setAccessible(accessible);
                        continue;
                    }
                    if (builder.length() != 0) {
                        builder.append(",");
                    }
                    builder.append(
                        Optional.of(column.name())
                            .filter(name -> !name.isEmpty())
                            .orElse(field.getName().toLowerCase())
                    ).append("=?");
                    boolean accessible = field.isAccessible();
                    field.setAccessible(true);
                    values.add(field.get(type));
                    field.setAccessible(accessible);
                }
            }
            values.addAll(whereValues);
            this.createQuery()
                .update(builder.toString())
                .where(where.toString())
                .execute(values.toArray(new Object[0]));
            return;
        }
        List<String> columns = new ArrayList<>();

        for (Field field : type.getClass().getDeclaredFields()) {
            if (field.isAnnotationPresent(Column.class)) {
                Column column = field.getAnnotation(Column.class);
                if (column.key() == Column.Key.PRIMARY && column.autoIncrement()) {
                    continue;
                }
                columns.add(
                    Optional.of(column.name())
                        .filter(name -> !name.isEmpty())
                        .orElse(field.getName().toLowerCase())
                );
                if (builder.length() != 0) {
                    builder.append(',');
                }
                builder.append('?');
                boolean accessible = field.isAccessible();
                field.setAccessible(true);
                values.add(field.get(type));
                field.setAccessible(accessible);
            }
        }
        this.createQuery()
            .insertInto(columns, builder.toString())
            .execute(values.toArray(new Object[0]));
    }

    protected boolean isPersisted(T type) throws Exception
    {
        List<Object> values = new ArrayList<>();
        StringBuilder builder = new StringBuilder();
        for (Field field : type.getClass().getDeclaredFields()) {
            if (field.isAnnotationPresent(Column.class)) {
                Column column = field.getAnnotation(Column.class);
                if (column.key() == Column.Key.PRIMARY) {
                    boolean accessible = field.isAccessible();
                    field.setAccessible(true);
                    if (column.autoIncrement() && field.get(type).equals(0)) {
                        field.setAccessible(accessible);
                        return false;
                    }
                    if (builder.length() != 0) {
                        builder.append(" AND ");
                    }
                    builder.append(
                        Optional.of(column.name())
                            .filter(name -> !name.isEmpty())
                            .orElse(field.getName().toLowerCase())
                    ).append("=?");
                    values.add(field.get(type));
                    field.setAccessible(accessible);
                }
            }
        }
        try (ResultSet resultSet = this.createQuery()
                .select("COUNT(*) count")
                .where(builder.toString())
                .getResult(values.toArray(new Object[0]))
        ) {
            if (resultSet.next()) {
                return resultSet.getLong("count") != 0;
            }
        }
        return false;
    }

    @Override
    public Query<ResultSet> createQuery() throws Exception
    {
        return this.database.query(this.getTableName());
    }

    @Override
    public Query<ResultSet> createQuery(String alias) throws Exception
    {
        return this.database.query(this.getTableName(), alias);
    }

    protected String getTableName() throws Exception
    {
        Class<T> clazz = (Class<T>) ((ParameterizedType) this.getClass().getGenericSuperclass())
            .getActualTypeArguments()[0];
        if (!clazz.isAnnotationPresent(Table.class)) {
            throw new MissingAnnotationException("Missing @Table annotation to " + clazz.getName() + " class !");
        }
        return Optional.of(clazz.getAnnotation(Table.class).name())
            .filter(name -> !name.isEmpty())
            .orElse(clazz.getName().toLowerCase());
    }

    protected T build(ResultSet resultSet) throws Throwable
    {
        Class<T> clazz = (Class<T>) ((ParameterizedType) this.getClass().getGenericSuperclass())
            .getActualTypeArguments()[0];
        if (!clazz.isAnnotationPresent(Table.class)) {
            throw new MissingAnnotationException("Missing @Table annotation to " + clazz.getName() + " class !");
        }
        T instance = clazz.newInstance();
        for (Field field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(Column.class)) {
                Column column = field.getAnnotation(Column.class);
                String columnName = Optional.of(column.name())
                    .filter(name -> !name.isEmpty())
                    .orElse(field.getName());
                boolean accessible = field.isAccessible();
                field.setAccessible(true);
                field.set(instance, resultSet.getObject(columnName));
                field.setAccessible(accessible);
            }
        }
        return instance;
    }
}
