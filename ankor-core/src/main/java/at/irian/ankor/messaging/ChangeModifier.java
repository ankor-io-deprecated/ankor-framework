package at.irian.ankor.messaging;

import at.irian.ankor.change.Change;
import at.irian.ankor.ref.Ref;

/**
 * The ChangeModifier optionally replaces a {@link at.irian.ankor.change.Change} before it is serialized to a remote partner.
 *
 * @author Manfred Geiler
 */
public interface ChangeModifier {

    Change modify(Change change, Ref changedProperty);

    public static class PassThrough implements ChangeModifier {
        @Override
        public Change modify(Change change, Ref changedProperty) {
            return change;
        }
    }
}
