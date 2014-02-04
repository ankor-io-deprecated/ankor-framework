package at.irian.ankor.messaging.modify;

import at.irian.ankor.change.Change;
import at.irian.ankor.change.ChangeType;
import at.irian.ankor.ref.Ref;
import at.irian.ankor.ref.impl.RefImplementor;
import at.irian.ankor.util.TypeCoercer;

/**
 * Makes sure that received untyped values are coerced to the correct java type of the corresponding Ref.
 *
 * @author Manfred Geiler
 */
public class CoerceTypeModifier extends AbstractModifier {
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(CoerceTypeModifier.class);

    private static final TypeCoercer TYPE_COERCER = new TypeCoercer();

    public CoerceTypeModifier(Modifier parent) {
        super(parent);
    }

    @Override
    public Change modifyAfterReceive(Change change, Ref changedProperty) {

        //noinspection StatementWithEmptyBody
        if (change.getType() == ChangeType.value) {

            Class<?> type;
            try {
                type = ((RefImplementor) changedProperty).getType();
            } catch (Exception e) {
                // could not determine property type
                LOG.debug("Could not determine type of {}", changedProperty);
                type = null;
            }

            Object coercedValue = coerce(change.getValue(), type);
            change = Change.valueChange(coercedValue);

        } else {
            // todo  support other change types?
            //       challenging because we must determine the generic type, ...
        }

        return super.modifyAfterReceive(change, changedProperty);
    }


    protected <T> T coerce(Object value, Class<T> type) {
        return TYPE_COERCER.coerceToType(value, type);
    }

}
