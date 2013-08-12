package at.irian.ankor.el;

import javax.el.ELContext;
import javax.el.ListELResolver;
import javax.el.PropertyNotFoundException;
import javax.el.PropertyNotWritableException;
import java.util.List;

/**
 * Like a ListELResolver but will not throw a <code>PropertyNotFoundException</code>
 * when setting out of upper bounds indices.
 *
 * @see ListELResolver
 * @see java.util.List
 */
public class FriendlyListELResolver extends ListELResolver {

    @Override
    public Class<?> getType(ELContext context,
                            Object base,
                            Object property) {

        if (context == null) {
            throw new NullPointerException();
        }

        if (base != null && base instanceof List) {
            context.setPropertyResolved(true);
            List list = (List) base;
            int index = toInteger(property);
            if (index < 0) {
                throw new PropertyNotFoundException();
            }
            return Object.class;
        }
        return null;
    }

    @Override
    public void setValue(ELContext context,
                         Object base,
                         Object property,
                         Object val) {

        if (context == null) {
            throw new NullPointerException();
        }

        if (base != null && base instanceof List) {
            context.setPropertyResolved(true);
            List list = (List) base;
            int index = toInteger(property);
            int size = list.size();
            try {
                while (size <= index) {
                    list.add(null);
                    size++;
                }
                list.set(index, val);
            } catch (UnsupportedOperationException ex) {
                throw new PropertyNotWritableException();
            } catch (IndexOutOfBoundsException ex) {
                throw ex;
            } catch (ClassCastException ex) {
                throw ex;
            } catch (NullPointerException ex) {
                throw ex;
            } catch (IllegalArgumentException ex) {
                throw ex;
            }
        }
    }

    private int toInteger(Object p) {
        if (p instanceof Integer) {
            return ((Integer) p).intValue();
        }
        if (p instanceof Character) {
            return ((Character) p).charValue();
        }
        if (p instanceof Boolean) {
            return ((Boolean) p).booleanValue()? 1: 0;
        }
        if (p instanceof Number) {
            return ((Number) p).intValue();
        }
        if (p instanceof String) {
            return Integer.parseInt((String) p);
        }
        throw new IllegalArgumentException();
    }
}
