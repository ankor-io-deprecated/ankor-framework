package at.irian.ankorsamples.animals.viewmodel.animal;

import at.irian.ankor.annotation.ActionListener;
import at.irian.ankor.annotation.ChangeListener;
import at.irian.ankor.delay.FloodControl;
import at.irian.ankor.messaging.AnkorIgnore;
import at.irian.ankor.pattern.AnkorPatterns;
import at.irian.ankor.ref.Ref;
import at.irian.ankor.viewmodel.ViewModelProperty;
import at.irian.ankorsamples.animals.domain.animal.Animal;
import at.irian.ankorsamples.animals.domain.animal.AnimalRepository;
import at.irian.ankorsamples.animals.viewmodel.TabNameCreator;

import java.util.Collections;
import java.util.List;

/**
* @author Thomas Spiegl
*/
@SuppressWarnings("UnusedDeclaration")
public class AnimalSearchModel {
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(AnimalSearchModel.class);

    @AnkorIgnore
    private final ViewModelProperty<String> tabName;
    @AnkorIgnore
    private final AnimalRepository animalRepository;
    @AnkorIgnore
    private final FloodControl reloadFloodControl;
    @AnkorIgnore
    private final Ref thisRef;

    private AnimalSearchFilter filter;

    private AnimalSelectItems selectItems;

    private List<Animal> animals;

    public AnimalSearchModel(Ref viewModelRef,
                             AnimalRepository animalRepository,
                             ViewModelProperty<String> tabName) {
        AnkorPatterns.initViewModel(this, viewModelRef);
        this.thisRef = viewModelRef;
        this.animalRepository = animalRepository;
        this.tabName = tabName;
        this.filter = new AnimalSearchFilter();
        this.selectItems = AnimalSelectItems.create(animalRepository.getAnimalTypes());
        this.animals = Collections.emptyList();
        this.reloadFloodControl = new FloodControl(viewModelRef, 500L);
    }

    public AnimalSearchFilter getFilter() {
        return filter;
    }

    public AnimalSelectItems getSelectItems() {
        return selectItems;
    }

    public List<Animal> getAnimals() {
        return animals;
    }

    public void setAnimals(List<Animal> animals) {
        this.animals = animals;
    }

    @ChangeListener(pattern = {".filter.**"})
    public void reloadAnimals() {
        reloadFloodControl.control(new Runnable() {
            @Override
            public void run() {
                LOG.info("RELOADING animals ...");
                List<Animal> animals = animalRepository.searchAnimals(filter,
                                                                      0,
                                                                      Integer.MAX_VALUE);

                thisRef.appendPath("animals").setValue(animals);

                LOG.info("... finished RELOADING");
                thisRef.root().appendPath("serverStatus").setValue("");
            }
        });
    }

    @ChangeListener(pattern = ".filter.name")
    public void onNameChanged() {
        String name = filter.getName();
        tabName.set(new TabNameCreator().createName("Animal Search", name));
    }

    @ChangeListener(pattern = ".filter.type")
    public void animalTypeChanged() {
        Ref familyRef = thisRef.appendPath("filter.family");
        Ref familiesRef = thisRef.appendPath("selectItems.families");
        new AnimalTypeChangeHandler(animalRepository).handleChange(filter.getType(), familyRef, familiesRef);
    }

    @ActionListener(name = "save")
    public void save() {
        String status;
        try {
            for (Animal animal : getAnimals()) {
                animalRepository.saveAnimal(animal);
            }
            status = "Animals successfully saved";
        } catch (Exception e) {
            status = "Error: " + e.getMessage();
            if (!(e instanceof IllegalArgumentException || e instanceof IllegalStateException)) {
                LOG.error("Error saving animals ", e);
            }
        }
        thisRef.root().appendPath("serverStatus").setValue(status);
    }

}
