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
public @interface AnkorBigMap {
    /**
     * @return minimal size of the map for switching to "big map" mode; a value of "-1" means "always" use a BigMap
     */
    int threshold() default -1;

    /**
     * @return number of map entries that are sent initially when the map is transferred for the first time
     */
    int initialSize() default 10;

    /**
     * @return Java type that may be instantiated (via default constructor) as "missing value substitute"
     */
    Class<?> missingValueSubstitute() default Null.class;

    static class Null {}
}
