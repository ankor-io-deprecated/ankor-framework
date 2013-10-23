package at.irian.ankor.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Manfred Geiler
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface AnkorWatched {
    /**
     * @return max number of list deltas to be sent, if more list elements are changed
     *         the whole list is sent to the remote system
     */
    int diffThreshold() default 20;
}
