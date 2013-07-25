package at.irian.ankorman.sample1.model.animal;

import at.irian.ankor.util.ObjectUtils;

/**
 * @author Thomas Spiegl
 */
public class TabNameCreator {

    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(TabNameCreator.class);

    private static final int MAX_LEN = 15;
    public String createName(String name, String value) {
        if (ObjectUtils.isEmpty(value)) {
            return name;
        } else {
            if (value.length() > MAX_LEN) {
                value = value.substring(0, MAX_LEN);
            }
            return String.format("%s (%s)", name, value);
        }
    }

}
