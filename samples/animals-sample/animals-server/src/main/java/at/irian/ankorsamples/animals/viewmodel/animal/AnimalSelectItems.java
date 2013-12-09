package at.irian.ankorsamples.animals.viewmodel.animal;

import at.irian.ankor.annotation.AutoSignal;
import at.irian.ankorsamples.animals.domain.animal.AnimalFamily;
import at.irian.ankorsamples.animals.domain.animal.AnimalType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Thomas Spiegl
 */
@SuppressWarnings("UnusedDeclaration")
@AutoSignal
public class AnimalSelectItems {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(AnimalSelectItems.class);

    private List<AnimalType> types;
    private List<AnimalFamily> families;

    public AnimalSelectItems() {
        this(Collections.<AnimalType>emptyList());
    }

    public AnimalSelectItems(List<AnimalType> types) {
        this.types = withNullItem(types);
        this.families = withNullItem(Collections.<AnimalFamily>emptyList());
    }

    public List<AnimalType> getTypes() {
        return types;
    }

    public void setTypes(List<AnimalType> types) {
        this.types = withNullItem(types);
    }

    public List<AnimalFamily> getFamilies() {
        return families;
    }

    public void setFamilies(List<AnimalFamily> families) {
        this.families = withNullItem(families);
    }

    private static <T> List<T> withNullItem(List<T> list) {
        List<T> selectItems = new ArrayList<>(list.size() + 1);
        selectItems.add(null);
        selectItems.addAll(list);
        return selectItems;
    }

}
