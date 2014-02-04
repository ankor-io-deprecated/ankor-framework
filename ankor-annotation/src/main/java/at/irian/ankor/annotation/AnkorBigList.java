package at.irian.ankor.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Manfred Geiler
 */
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface AnkorBigList {
    /**
     * @return minimal size of the list for switching to "big list" mode; a value of "-1" means "always" use a BigList
     */
    int threshold() default -1;

    /**
     * @return number of list entries that are sent initially when the list is transferred for the first time
     */
    int initialSize() default 10;

    /**
     * @return number of list entries that are transferred as a block on each "missing entries request"
     */
    int chunkSize() default 10;

    /**
     * @return Java type that may be instantiated (via default constructor) as "missing element substitute"
     */
    Class<?> missingElementSubstitute() default Null.class;

    static class Null {}
}
