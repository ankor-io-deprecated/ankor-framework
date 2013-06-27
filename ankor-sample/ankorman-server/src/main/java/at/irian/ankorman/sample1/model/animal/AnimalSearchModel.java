package at.irian.ankorman.sample1.model.animal;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
* @author Thomas Spiegl
*/
public class AnimalSearchModel {

    private AnimalSearchFilter filter;
    @JsonTypeInfo(use = JsonTypeInfo.Id.NONE, defaultImpl = AnimalSelectItems.class)
    private AnimalSelectItems selectItems;
    @JsonTypeInfo(use = JsonTypeInfo.Id.NONE, defaultImpl = Animal.class)
    private Data<Animal> animals;


    @SuppressWarnings("UnusedDeclaration")
    protected AnimalSearchModel() {
    }

    public AnimalSearchModel(AnimalSelectItems selectItems) {
        this.filter = new AnimalSearchFilter();
        this.selectItems = selectItems;
        this.animals = new Data<Animal>(new Paginator(0, 5));
    }

    public AnimalSearchFilter getFilter() {
        return filter;
    }


    public AnimalSelectItems getSelectItems() {
        return selectItems;
    }

    public void setSelectItems(AnimalSelectItems selectItems) {
        this.selectItems = selectItems;
    }

    public Data<Animal> getAnimals() {
        return animals;
    }

    public void setAnimals(Data<Animal> animals) {
        this.animals = animals;
    }
}
