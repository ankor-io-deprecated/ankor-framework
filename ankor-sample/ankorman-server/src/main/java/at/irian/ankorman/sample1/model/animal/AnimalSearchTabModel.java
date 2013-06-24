package at.irian.ankorman.sample1.model.animal;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.util.List;

/**
* @author Thomas Spiegl
*/
public class AnimalSearchTabModel {

    private AnimalSearchFilter filter;
    @JsonTypeInfo(use = JsonTypeInfo.Id.NONE, defaultImpl = AnimalSelectItems.class)
    private AnimalSelectItems selectItems;
    @JsonTypeInfo(use = JsonTypeInfo.Id.NONE, defaultImpl = Animal.class)
    private List<Animal> animals;

    @SuppressWarnings("UnusedDeclaration")
    protected AnimalSearchTabModel() {
    }

    public AnimalSearchTabModel(AnimalSelectItems selectItems) {
        this.filter = new AnimalSearchFilter();
        this.selectItems = selectItems;
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

    public AnimalSelectItems getSelectItems() {
        return selectItems;
    }

    public void setSelectItems(AnimalSelectItems selectItems) {
        this.selectItems = selectItems;
    }
}
