package at.irian.ankor.core.test.animal;

import at.irian.ankor.core.listener.ModelActionListener;
import at.irian.ankor.core.ref.ModelRef;

import java.util.ArrayList;
import java.util.List;

/**
 * @author MGeiler (Manfred Geiler)
 */
public class AnimalSearchActionListener implements ModelActionListener {
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(AnimalSearchActionListener.class);

    @Override
    public void handleModelAction(ModelRef containerRef, String action) {
        if (action.equals("search")) {
            Object container = containerRef.getValue();

            if (container instanceof AnimalSearchContainer) {
                LOG.info("Animal search action");
                AnimalSearchContainer animalSearchContainer = (AnimalSearchContainer) container;

                if (animalSearchContainer.getFilter().getName().equals("A*")) {

                    List<Animal> animals = new ArrayList<Animal>();
                    animals.add(new Animal("Adler", AnimalType.Bird));
                    animals.add(new Animal("Amsel", AnimalType.Bird));

                    containerRef.sub("resultList").setValue(animals);
                }
            }

        }
    }
}
