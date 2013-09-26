package at.irian.ankorsamples.animals.viewmodel.animal;

import at.irian.ankor.ref.Ref;
import at.irian.ankorsamples.animals.domain.animal.AnimalFamily;
import at.irian.ankorsamples.animals.domain.animal.AnimalRepository;
import at.irian.ankorsamples.animals.domain.animal.AnimalType;

import java.util.Collections;
import java.util.List;

/**
* @author Thomas Spiegl
*/
class AnimalTypeChangeHandler {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(AnimalTypeChangeHandler.class);

    private final AnimalRepository animalRepository;

    AnimalTypeChangeHandler(AnimalRepository animalRepository) {
        this.animalRepository = animalRepository;
    }

    public void handleChange(AnimalType type, Ref familyRef, Ref selectItemsFamiliesRef) {
        List<AnimalFamily> families;
        if (type != null) {
            families = animalRepository.getAnimalFamilies(type);
        } else {
            families = Collections.emptyList();
        }
        selectItemsFamiliesRef.setValue(AnimalSelectItems.createSelectItemsFrom(families));

        //noinspection SuspiciousMethodCalls
        if (!families.contains(familyRef.getValue())) {
            familyRef.setValue(null);
        }
    }
}
