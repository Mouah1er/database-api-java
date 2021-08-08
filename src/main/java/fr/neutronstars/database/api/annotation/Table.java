package fr.neutronstars.database.api.annotation;

import fr.neutronstars.database.api.Engine;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
public @interface Table
{
    String name() default "";
    String interclassement() default "utf8mb4_general_ci";
    Engine engine() default Engine.INNODB;
    String comment() default "";
}
