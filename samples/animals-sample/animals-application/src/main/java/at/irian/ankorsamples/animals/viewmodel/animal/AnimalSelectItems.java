package at.irian.ankorsamples.animals.viewmodel.animal;

import at.irian.ankor.annotation.AnkorInit;
import at.irian.ankor.annotation.AutoSignal;
import at.irian.ankorsamples.animals.domain.AnimalFamily;
import at.irian.ankorsamples.animals.domain.AnimalType;

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

    @AnkorInit
    public void initEmpty() {
        init(Collections.<AnimalType>emptyList(),
             Collections.<AnimalFamily>emptyList());
    }

    @AnkorInit
    public void init(List<AnimalType> types, List<AnimalFamily> families) {
        this.types = withNullItem(types);
        this.families = withNullItem(families);
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
