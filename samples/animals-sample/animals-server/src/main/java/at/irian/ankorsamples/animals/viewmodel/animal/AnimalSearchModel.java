package at.irian.ankorsamples.animals.viewmodel.animal;

import at.irian.ankor.annotation.ActionListener;
import at.irian.ankor.annotation.ChangeListener;
import at.irian.ankor.delay.FloodControl;
import at.irian.ankor.messaging.AnkorIgnore;
import at.irian.ankor.pattern.AnkorPatterns;
import at.irian.ankor.ref.Ref;
import at.irian.ankor.ref.TypedRef;
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

    private final TypedRef<String> tabNameRef;

    @AnkorIgnore
    private final AnimalRepository animalRepository;
    @AnkorIgnore
    private final FloodControl reloadFloodControl;

    private AnimalSearchFilter filter;

    private AnimalSelectItems selectItems;

    private List<Animal> animals;

    public AnimalSearchModel(Ref viewModelRef,
                             AnimalRepository animalRepository,
                             TypedRef<String> tabNameRef) {
        AnkorPatterns.initViewModel(this, viewModelRef);
        this.animalRepository = animalRepository;
        this.tabNameRef = tabNameRef;
        this.filter = new AnimalSearchFilter();
        this.selectItems = AnimalSelectItems.create(animalRepository.getAnimalTypes());
        this.animals = Collections.emptyList();
        this.reloadFloodControl = new FloodControl(viewModelRef, 500L);

        reloadAnimals(viewModelRef.root(), viewModelRef);
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

    @ChangeListener(pattern = {"(*).**.(@).filter.**"})
    public void reloadAnimals(final Ref rootRef, final Ref modelRef) {
        reloadFloodControl.control(new Runnable() {
            @Override
            public void run() {
                LOG.info("RELOADING animals ...");
                setAnimals(animalRepository.searchAnimals(filter, 0, Integer.MAX_VALUE));
                LOG.info("... finished RELOADING");
                modelRef.appendPath("animals").signalChange();
                rootRef.appendPath("serverStatus").setValue("");
            }
        });
    }

    @ChangeListener(pattern = ".filter.name")
    public void onNameChanged() {
        String name = filter.getName();
        tabNameRef.setValue(new TabNameCreator().createName("Animal Search", name));
    }

    @ChangeListener(pattern = "(@).filter.type")
    public void animalTypeChanged(final Ref modelRef) {
        Ref familyRef = modelRef.appendPath("filter.family");
        Ref familiesRef = modelRef.appendPath("selectItems.families");
        new AnimalTypeChangeHandler(animalRepository).handleChange(filter.getType(), familyRef, familiesRef);
    }

    @ActionListener(name = "save", pattern = "(*).**.@")
    public void save(Ref rootRef) {
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
        rootRef.appendPath("serverStatus").setValue(status);
    }

}
