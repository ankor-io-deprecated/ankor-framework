package at.irian.ankor.sample.fx.view.model;

import at.irian.ankor.sample.fx.server.model.Animal;

/**
 * @author Thomas Spiegl
 */
public class AnimalDetailTab {

    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(AnimalDetailTab.class);
    private Animal animal;

    public Animal getAnimal() {
        return animal;
    }

    public void setAnimal(Animal animal) {
        this.animal = animal;
    }
}
