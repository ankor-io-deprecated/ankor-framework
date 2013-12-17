package at.irian.ankorsamples.animals.viewmodel.animal;

import at.irian.ankor.annotation.ActionListener;
import at.irian.ankor.annotation.AnkorWatched;
import at.irian.ankor.annotation.ChangeListener;
import at.irian.ankor.annotation.Param;
import at.irian.ankor.big.AnkorBigList;
import at.irian.ankor.delay.FloodControl;
import at.irian.ankor.ref.Ref;
import at.irian.ankor.ref.TypedRef;
import at.irian.ankor.viewmodel.ViewModels;
import at.irian.ankor.viewmodel.watch.ExtendedList;
import at.irian.ankor.viewmodel.watch.ExtendedListWrapper;
import at.irian.ankorsamples.animals.domain.animal.Animal;
import at.irian.ankorsamples.animals.domain.animal.AnimalFamily;
import at.irian.ankorsamples.animals.domain.animal.AnimalRepository;
import at.irian.ankorsamples.animals.domain.animal.AnimalType;
import at.irian.ankorsamples.animals.viewmodel.PanelNameCreator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static at.irian.ankor.viewmodel.factory.BeanFactories.newPropertyInstance;

/**
* @author Thomas Spiegl
*/
@SuppressWarnings("UnusedDeclaration")
public class AnimalSearchModel {
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(AnimalSearchModel.class);

    private final TypedRef<String> panelNameRef;
    private final TypedRef<String> serverStatusRef;
    private final Ref i18nResourcesRef;
    private final AnimalRepository animalRepository;
    private final AnimalSearchFilter filter;
    private final AnimalSelectItems selectItems;
    private final FloodControl reloadFloodControl;

    @AnkorBigList(missingElementSubstitute = EmptyAnimal.class,
                  threshold = 500,
                  initialSize = 10,
                  chunkSize = 10)
    @AnkorWatched(diffThreshold = 20)
    private ExtendedList<Animal> animals;

    public AnimalSearchModel(TypedRef<String> panelNameRef,
                             TypedRef<String> serverStatusRef,
                             Ref i18nResourcesRef,
                             AnimalRepository animalRepository) {
        this.panelNameRef = panelNameRef;
        this.serverStatusRef = serverStatusRef;
        this.i18nResourcesRef = i18nResourcesRef;
        this.animalRepository = animalRepository;

        this.filter = newPropertyInstance(AnimalSearchFilter.class, this, "filter");
        this.selectItems = newPropertyInstance(AnimalSelectItems.class, this, "selectItems",
                                               animalRepository.getAnimalTypes(),
                                               Collections.emptyList());

        this.animals = new ExtendedListWrapper<>(new ArrayList<Animal>());

        this.reloadFloodControl = new FloodControl(ViewModels.ref(this), 500L);  // todo:  flood control with annotation
    }

    public AnimalSearchFilter getFilter() {
        return filter;
    }

    public AnimalSelectItems getSelectItems() {
        return selectItems;
    }

    public ExtendedList<Animal> getAnimals() {
        return animals;
    }

    @ChangeListener(pattern = {".filter.**"})
    public void reloadAnimals() {
        reloadFloodControl.control(new Runnable() {
            @Override
            public void run() {

                reloadAnimalsImmediately();

                // reset server status display
                serverStatusRef.setValue("");
            }
        });
    }

    private void reloadAnimalsImmediately() {
        // get new list from database
        LOG.info("RELOADING animals ...");
        List<Animal> newAnimalsList = animalRepository.searchAnimals(filter, 0, Integer.MAX_VALUE);
        LOG.info("... finished RELOADING");

        animals.setAll(newAnimalsList);
    }

    @ChangeListener(pattern = {".filter.name", "root.locale"})
    public void onNameOrLocaleChanged() {
        panelNameRef.setValue(getPanelName());
    }

    public String getPanelName() {
        String name = filter.getName();
        return new PanelNameCreator().createName(i18nResourcesRef.appendLiteralKey("SearchForAnimal").<String>getValue(), name);
    }

    @ChangeListener(pattern = ".filter.type")
    public void animalTypeChanged() {

        AnimalType type = filter.getType();
        List<AnimalFamily> families;
        if (type != null) {
            families = animalRepository.getAnimalFamilies(type);
        } else {
            families = Collections.emptyList();
        }
        selectItems.setFamilies(families);

        //noinspection SuspiciousMethodCalls
        if (!families.contains(filter.getFamily())) {
            filter.setFamily(null);
        }
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
        serverStatusRef.setValue(status);
    }

    @ActionListener(name = "delete")
    public void delete(@Param("uuid") String uuid) {
        animalRepository.deleteAnimal(uuid);
        reloadAnimalsImmediately();
        serverStatusRef.setValue("Animal deleted");
    }

    public static class EmptyAnimal extends Animal {
        public EmptyAnimal() {
            super("...", null, null);
        }
    }
}
