package at.irian.ankor.viewmodel.watch;

import java.util.Collection;
import java.util.List;

/**
 * {@link List} interface extension, inspired by JavaFX {@link javafx.collections.ObservableList}
 *
 * @author Manfred Geiler
 */
@SuppressWarnings("UnusedDeclaration")
public interface ExtendedList<E> extends List<E> {

    boolean addAll(E... elements);

    boolean setAll(E... elements);

    boolean setAll(Collection<? extends E> col);

    boolean removeAll(E... elements);

    boolean retainAll(E... elements);

    void remove(int from, int to);

}
