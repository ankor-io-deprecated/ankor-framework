package at.irian.ankorsamples.animals.viewmodel.animal;

import at.irian.ankorsamples.animals.domain.animal.AnimalFamily;
import at.irian.ankorsamples.animals.domain.animal.AnimalType;

import java.util.List;

/**
 * @author Thomas Spiegl
 */
public class AnimalSelectItems {

    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(AnimalSelectItems.class);

    private List<AnimalType> types;
    private List<AnimalFamily> families;

    public AnimalSelectItems(List<AnimalType> types, List<AnimalFamily> families) {
        this.types = types;
        this.families = families;
    }

    public List<AnimalType> getTypes() {
        return types;
    }

    public void setTypes(List<AnimalType> types) {
        this.types = types;
    }

    public List<AnimalFamily> getFamilies() {
        return families;
    }

    public void setFamilies(List<AnimalFamily> families) {
        this.families = families;
    }

}
