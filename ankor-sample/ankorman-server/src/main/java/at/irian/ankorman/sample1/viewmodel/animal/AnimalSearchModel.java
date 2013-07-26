package at.irian.ankorman.sample1.viewmodel.animal;

import at.irian.ankor.annotation.ActionListener;
import at.irian.ankor.annotation.ChangeListener;
import at.irian.ankor.model.ViewModelBase;
import at.irian.ankor.model.ViewModelProperty;
import at.irian.ankor.ref.Ref;
import at.irian.ankorman.sample1.domain.animal.Animal;
import at.irian.ankorman.sample1.server.AnimalRepository;
import at.irian.ankorman.sample1.viewmodel.TabNameCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
* @author Thomas Spiegl
*/
public class AnimalSearchModel extends ViewModelBase {
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(AnimalSearchModel.class);

    @JsonIgnore
    private final ViewModelProperty<String> tabName;

    @JsonIgnore
    private final AnimalRepository animalRepository;

    private AnimalSearchFilter filter;

    private AnimalSelectItems selectItems;

    private Data<Animal> animals;

    public AnimalSearchModel(Ref viewModelRef,
                             AnimalRepository animalRepository, AnimalSelectItems selectItems,
                             ViewModelProperty<String> tabName) {
        super(viewModelRef);
        this.animalRepository = animalRepository;
        this.tabName = tabName;
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

    @ChangeListener(pattern = {"**.<AnimalSearchModel>.filter.**",
                               "**.<AnimalSearchModel>.animals.paginator.**"})
    public void reloadAnimals() {
        // TODO how to load data async and update the animals ref?
        LOG.info("RELOADING animals ...");
        Paginator paginator = getAnimals().getPaginator();
        paginator.reset();
        Data<Animal> animals = animalRepository.searchAnimals(filter, paginator.getFirst(), paginator.getMaxResults());

        thisRef().append("animals").setValue(animals);

        LOG.info("... finished RELOADING");
        thisRef().root().append("serverStatus").setValue("");
    }

    @ChangeListener(pattern = "**.<AnimalSearchModel>.filter.name")
    public void onNameChanged() {
        String name = filter.getName();
        tabName.set(new TabNameCreator().createName("Animal Search", name));
    }

    @ChangeListener(pattern = "**.<AnimalSearchModel>.filter.type")
    public void animalTypeChanged() {
        Ref familyRef = thisRef().append("filter.family");
        Ref familiesRef = thisRef().append("selectItems.families");
        new AnimalTypeChangeHandler().handleChange(familyRef, familiesRef, filter.getType());
    }

    @ActionListener(name = "save")
    public void save() {
        String status;
        try {
            for (Animal animal : getAnimals().getRows()) {
                animalRepository.saveAnimal(animal);
            }
            status = "Animals successfully saved";
        } catch (Exception e) {
            status = "Error: " + e.getMessage();
            if (!(e instanceof IllegalArgumentException || e instanceof IllegalStateException)) {
                LOG.error("Error saving animals ", e);
            }
        }
        thisRef().root().append("serverStatus").setValue(status);
    }

}
