package at.irian.ankorman.sample1.model.animal;

import at.irian.ankor.ref.Ref;

import java.util.ArrayList;
import java.util.List;

/**
* @author Thomas Spiegl
*/
public class AnimalTypeChangeHandler {

    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(AnimalTypeChangeHandler.class);

    public void handleChange(Ref familyRef, Ref familiesRef, AnimalType type) {
        LOG.info("animalTypeChanged");
        List<AnimalFamily> families;
        if (type != null) {
            families = new ArrayList<AnimalFamily>();
            switch (type) {
                case Bird:
                    families.add(AnimalFamily.Accipitridae);
                    break;
                case Fish:
                    families.add(AnimalFamily.Esocidae);
                    families.add(AnimalFamily.Salmonidae);
                    break;
                case Mammal:
                    families.add(AnimalFamily.Balaenopteridae);
                    families.add(AnimalFamily.Felidae);
                    break;
            }
        } else {
            families = new ArrayList<AnimalFamily>(0);
        }
        familiesRef.setValue(families);
        familyRef.setValue(null);
    }
}
