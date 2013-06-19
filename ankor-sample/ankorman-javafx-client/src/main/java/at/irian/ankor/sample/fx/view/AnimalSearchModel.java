package at.irian.ankor.sample.fx.view;

import at.irian.ankor.sample.fx.model.Animal;
import at.irian.ankor.sample.fx.model.AnimalSearchFilter;

import java.util.List;

/**
* @author Thomas Spiegl
*/
public class AnimalSearchModel {

    private AnimalSearchFilter filter;

    private List<Animal> animals;

    public AnimalSearchModel() {
        this.filter = new AnimalSearchFilter();
    }

    public AnimalSearchFilter getFilter() {
        return filter;
    }

    public List<Animal> getAnimals() {
        return animals;
    }

    public void setAnimals(List<Animal> animals) {
        this.animals = animals;
    }
}
