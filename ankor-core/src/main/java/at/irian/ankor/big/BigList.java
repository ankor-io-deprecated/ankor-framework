package at.irian.ankor.big;

import java.util.List;

/**
 * @author Manfred Geiler
 */
@SuppressWarnings("UnusedDeclaration")
public interface BigList<E> extends List<E> {

    /**
     * Clears all internally stored elements from memory.
     */
    void reset();

    /**
     * Removes all obsolete internal references that are no longer referencing valid objects.
     * A cleanup does not alter the content of the List as it is seen from the outside.
     * However, a long-living BigList might waste memory after some time if there are many SoftReferences left
     * that are no longer pointing to valid objects in memory. Periodically doing a cleanup might help
     * keeping memory consumptions lower.
     */
    void cleanup();

    /**
     * Checks if a certain indexed element is available in memory or missing.
     * @param index  Index
     * @return true if the list element with the given index is available in memory
     */
    boolean isAvailable(int index);

}
