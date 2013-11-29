package at.irian.ankor.el;

import java.beans.PropertyEditorSupport;
import java.util.Locale;

/**
 * @author Manfred Geiler
 */
public class LocalePropertyEditor extends PropertyEditorSupport {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(LocalePropertyEditor.class);

    public void setAsText(java.lang.String s) throws java.lang.IllegalArgumentException {
        if (s == null) {
            setValue(Locale.getDefault());
        } else {
            setValue(new Locale(s));
        }
    }

}
