package at.irian.ankor.sample.fx.view.model;

import at.irian.ankor.sample.fx.server.model.Animal;
import at.irian.ankor.sample.fx.server.model.AnimalSearchFilter;

import java.util.List;

/**
* @author Thomas Spiegl
*/
public class AnimalSearchTab {

    private AnimalSearchFilter filter;

    private List<Animal> animals;

    public AnimalSearchTab() {
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
