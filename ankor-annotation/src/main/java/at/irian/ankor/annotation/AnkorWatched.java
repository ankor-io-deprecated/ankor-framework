package at.irian.ankor.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a view model property of type {@link java.util.List} as a "watched list".
 * Watched list properties are automatically getting wrapped by a {@link at.irian.ankor.viewmodel.watch.WatchedList}
 * during view model bean post processing.
 *
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
