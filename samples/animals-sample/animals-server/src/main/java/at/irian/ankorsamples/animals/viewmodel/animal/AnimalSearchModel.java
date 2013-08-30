package at.irian.ankorsamples.animals.viewmodel.animal;

import at.irian.ankor.annotation.ActionListener;
import at.irian.ankor.annotation.ChangeListener;
import at.irian.ankor.delay.FloodControl;
import at.irian.ankor.messaging.AnkorIgnore;
import at.irian.ankor.ref.Ref;
import at.irian.ankor.viewmodel.ViewModelBase;
import at.irian.ankor.viewmodel.ViewModelProperty;
import at.irian.ankorsamples.animals.domain.animal.Animal;
import at.irian.ankorsamples.animals.domain.animal.AnimalRepository;
import at.irian.ankorsamples.animals.viewmodel.TabNameCreator;

/**
* @author Thomas Spiegl
*/
@SuppressWarnings("UnusedDeclaration")
public class AnimalSearchModel extends ViewModelBase {
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(AnimalSearchModel.class);

    @AnkorIgnore
    private final ViewModelProperty<String> tabName;

    @AnkorIgnore
    private final AnimalRepository animalRepository;

    @AnkorIgnore
    private final FloodControl reloadFloodControl;

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
        this.animals = new Data<>(new Paginator(0, 5));
        this.reloadFloodControl = new FloodControl(viewModelRef, 500L);
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
        reloadFloodControl.control(new Runnable() {
            @Override
            public void run() {
                LOG.info("RELOADING animals ...");
                Paginator paginator = getAnimals().getPaginator();
                paginator.reset();
                Data<Animal> animals = animalRepository.searchAnimals(filter,
                                                                      paginator.getFirst(),
                                                                      paginator.getMaxResults());

                thisRef().appendPath("animals").setValue(animals);

                LOG.info("... finished RELOADING");
                thisRef().root().appendPath("serverStatus").setValue("");
            }
        });
    }

    @ChangeListener(pattern = "**.<AnimalSearchModel>.filter.name")
    public void onNameChanged() {
        String name = filter.getName();
        tabName.set(new TabNameCreator().createName("Animal Search", name));
    }

    @ChangeListener(pattern = "**.<AnimalSearchModel>.filter.type")
    public void animalTypeChanged() {
        Ref familyRef = thisRef().appendPath("filter.family");
        Ref familiesRef = thisRef().appendPath("selectItems.families");
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
        thisRef().root().appendPath("serverStatus").setValue(status);
    }

}
