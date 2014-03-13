package at.irian.ankor.base;

import java.beans.PropertyEditor;
import java.beans.PropertyEditorManager;
import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * Coerce an Object to a specific java type.
 * Main usage is to coerce values received from untyped remote systems to strongly typed Java types.
 * The coerce mechanism is heavily influenced by and follows the EL specification (JSR 245, JSR 341).
 *
 * @author Manfred Geiler
 */
public class TypeCoercer {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(TypeCoercer.class);

    @SuppressWarnings("unchecked")
    public <T> T coerceToType(Object obj, Class<T> type) {
        if (type == null ||
            Object.class.equals(type) ||
            (obj != null && type.isAssignableFrom(obj.getClass()))) {
            return (T)obj;
        }
        if (isStringType(type)) {
            return (T)coerceToString(obj);
        }
        if (isNumberType(type)) {
            return (T)coerceToNumber(obj, type);
        }
        if (isCharacterType(type)) {
            return (T)coerceToCharacter(obj);
        }
        if (isBooleanType(type)) {
            return (T)coerceToBoolean(obj);
        }
        if (type.isEnum()) {
            return (T)coerceToEnum(obj, type);
        }
        if (obj == null) {
            return null;
        }
        if (obj instanceof String) {
            PropertyEditor editor = PropertyEditorManager.findEditor(type);
            if (editor != null) {
                editor.setAsText((String) obj);
                return (T)editor.getValue();
            }
        }
        throw new IllegalArgumentException("Could not convert " + obj + " of type " + obj.getClass() + " to type " + type);
    }

    protected <T> boolean isStringType(Class<T> type) {
        return String.class.equals(type);
    }

    protected <T> boolean isCharacterType(Class<T> type) {
        return Character.class.equals(type) || Character.TYPE == type;
    }

    protected <T> boolean isBooleanType(Class<T> type) {
        return Boolean.class.equals(type) || Boolean.TYPE == type;
    }

    protected boolean isNumber(Object obj) {
        return (obj != null && isNumberType(obj.getClass()));
    }

    protected boolean isNumberType(Class type) {
        return type == (java.lang.Long.class) ||
               type == Long.TYPE ||
               type == (java.lang.Double.class) ||
               type == Double.TYPE ||
               type == (java.lang.Byte.class) ||
               type == Byte.TYPE ||
               type == (java.lang.Short.class) ||
               type == Short.TYPE ||
               type == (java.lang.Integer.class) ||
               type == Integer.TYPE ||
               type == (java.lang.Float.class) ||
               type == Float.TYPE ||
               type == (java.math.BigInteger.class) ||
               type == (java.math.BigDecimal.class);
    }

    protected String coerceToString(final Object obj) {
        if (obj == null) {
            return "";
        } else if (obj instanceof String) {
            return (String) obj;
        } else if (obj instanceof Enum) {
            return ((Enum) obj).name();
        } else {
            return obj.toString();
        }
    }

    protected Number coerceToNumber(final Object obj, final Class type) throws IllegalArgumentException {
        if (obj == null || "".equals(obj)) {
            return coerceNumberToNumber(0, type);
        }
        if (obj instanceof String) {
            return coerceStringToNumber((String) obj, type);
        }
        if (isNumber(obj)) {
            if (obj.getClass().equals(type)) {
                return (Number) obj;
            }
            return coerceNumberToNumber((Number) obj, type);
        }

        if (obj instanceof Character) {
            return coerceNumberToNumber((short) ((Character) obj).charValue(), type);
        }

        throw new IllegalArgumentException("Could not convert " + obj + " of type " + obj.getClass() + " to type " + type);
    }


    protected Number coerceNumberToNumber(final Number number, final Class type) throws IllegalArgumentException {
        if (Long.TYPE == type || Long.class.equals(type)) {
            return number.longValue();
        }
        if (Double.TYPE == type || Double.class.equals(type)) {
            return number.doubleValue();
        }
        if (Integer.TYPE == type || Integer.class.equals(type)) {
            return number.intValue();
        }
        if (BigInteger.class.equals(type)) {
            if (number instanceof BigDecimal) {
                return ((BigDecimal) number).toBigInteger();
            }
            if (number instanceof BigInteger) {
                return number;
            }
            return BigInteger.valueOf(number.longValue());
        }
        if (BigDecimal.class.equals(type)) {
            if (number instanceof BigDecimal) {
                return number;
            }
            if (number instanceof BigInteger) {
                return new BigDecimal((BigInteger) number);
            }
            if (number instanceof Long) {
                return new BigDecimal((Long) number);
            }
            return new BigDecimal(number.doubleValue());
        }
        if (Byte.TYPE == type || Byte.class.equals(type)) {
            return number.byteValue();
        }
        if (Short.TYPE == type || Short.class.equals(type)) {
            return number.shortValue();
        }
        if (Float.TYPE == type || Float.class.equals(type)) {
            return number.floatValue();
        }

        throw new IllegalArgumentException("Could not convert " + number + " of type " + number.getClass() + " to type " + Number.class);
    }

    protected Number coerceStringToNumber(final String val, final Class type) throws IllegalArgumentException {
        if (Long.TYPE == type || Long.class.equals(type)) {
            return Long.valueOf(val);
        }
        if (Integer.TYPE == type || Integer.class.equals(type)) {
            return Integer.valueOf(val);
        }
        if (Double.TYPE == type || Double.class.equals(type)) {
            return Double.valueOf(val);
        }
        if (BigInteger.class.equals(type)) {
            return new BigInteger(val);
        }
        if (BigDecimal.class.equals(type)) {
            return new BigDecimal(val);
        }
        if (Byte.TYPE == type || Byte.class.equals(type)) {
            return Byte.valueOf(val);
        }
        if (Short.TYPE == type || Short.class.equals(type)) {
            return Short.valueOf(val);
        }
        if (Float.TYPE == type || Float.class.equals(type)) {
            return Float.valueOf(val);
        }

        throw new IllegalArgumentException("Could not convert " + val + " of type " + val.getClass() + " to type " + Number.class);
    }


    protected Character coerceToCharacter(final Object obj) throws IllegalArgumentException {
        if (obj == null || "".equals(obj)) {
            return (char) 0;
        }
        if (obj instanceof String) {
            return ((String) obj).charAt(0);
        }
        if (isNumber(obj)) {
            return (char) ((Number) obj).shortValue();
        }
        if (obj instanceof Character) {
            return (Character) obj;
        }

        throw new IllegalArgumentException("Could not convert " + obj + " of type " + obj.getClass() + " to type " + Character.class);
    }

    protected Boolean coerceToBoolean(final Object obj) throws IllegalArgumentException {
        if (obj == null || "".equals(obj)) {
            return Boolean.FALSE;
        }
        if (obj instanceof Boolean) {
            return (Boolean) obj;
        }
        if (obj instanceof String) {
            return Boolean.valueOf((String) obj);
        }

        throw new IllegalArgumentException("Could not convert " + obj + " of type " + obj.getClass() + " to type " + Boolean.class);
    }

    public Enum coerceToEnum(final Object obj, Class type) {
        if (obj == null || "".equals(obj)) {
            return null;
        }
        if (obj.getClass().isEnum()) {
            return (Enum)obj;
        }
        return Enum.valueOf(type, obj.toString());
    }

}
