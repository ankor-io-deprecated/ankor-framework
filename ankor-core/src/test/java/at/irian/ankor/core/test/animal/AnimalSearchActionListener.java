package at.irian.ankor.core.test.animal;

import at.irian.ankor.core.listener.ModelActionListener;
import at.irian.ankor.core.ref.ModelRef;

import java.util.ArrayList;

/**
 * @author MGeiler (Manfred Geiler)
 */
public class AnimalSearchActionListener implements ModelActionListener {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(AnimalSearchActionListener.class);

    @Override
    public void handleModelAction(ModelRef containerRef, String action) {
        if (action.equals("search")) {
            AnimalSearchContainer container = containerRef.getValue();

            if (container.getFilter().getName().equals("A*")) {

                ArrayList<Animal> animals = new ArrayList<Animal>();
                animals.add(new Animal("Adler", AnimalType.Bird));
                animals.add(new Animal("Amsel", AnimalType.Bird));

                containerRef.with("resultList").setValue(animals);
            }

        }
    }
}
