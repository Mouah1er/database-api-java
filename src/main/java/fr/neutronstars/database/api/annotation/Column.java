package fr.neutronstars.database.api.annotation;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Documented
public @interface Column
{
    String name() default "";
    String type() default "varchar(255)";
    Key key() default Key.NONE;
    String interclassement() default "";
    boolean nullable() default false;
    boolean autoIncrement() default false;
    String comment() default "";

    enum Key
    {
        PRIMARY,
        UNIQUE,
        INDEX,
        FULLTEXT,
        SPATIAL,
        NONE;
    }
}
