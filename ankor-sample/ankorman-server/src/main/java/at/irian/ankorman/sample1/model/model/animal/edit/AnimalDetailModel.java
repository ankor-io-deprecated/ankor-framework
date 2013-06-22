package at.irian.ankorman.sample1.model.model.animal.edit;

import at.irian.ankorman.sample1.model.model.animal.Animal;

/**
 * @author Thomas Spiegl
 */
public class AnimalDetailModel {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(AnimalDetailModel.class);

    private Animal animal;

    public Animal getAnimal() {
        return animal;
    }

    public void setAnimal(Animal animal) {
        this.animal = animal;
    }
}
