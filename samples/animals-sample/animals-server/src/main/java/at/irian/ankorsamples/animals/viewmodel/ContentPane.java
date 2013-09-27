package at.irian.ankorsamples.animals.viewmodel;

import at.irian.ankor.annotation.ActionListener;
import at.irian.ankor.messaging.AnkorIgnore;
import at.irian.ankor.pattern.AnkorPatterns;
import at.irian.ankor.ref.Ref;
import at.irian.ankor.ref.TypedRef;
import at.irian.ankorsamples.animals.domain.animal.Animal;
import at.irian.ankorsamples.animals.domain.animal.AnimalRepository;
import at.irian.ankorsamples.animals.viewmodel.animal.AnimalDetailModel;
import at.irian.ankorsamples.animals.viewmodel.animal.AnimalSearchModel;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Thomas Spiegl
 */
@SuppressWarnings("UnusedDeclaration")
public class ContentPane {

    private final Ref myRef;
    private final TypedRef<String> serverStatusRef;

    @AnkorIgnore private final AnimalRepository animalRepository;

    @AnkorIgnore private int panelIdCounter;
    private final Map<String, Panel> panels;

    protected ContentPane(Ref contentPaneRef,
                          TypedRef<String> serverStatusRef,
                          AnimalRepository animalRepository) {
        this.myRef = contentPaneRef;
        this.serverStatusRef = serverStatusRef;
        this.animalRepository = animalRepository;
        this.panelIdCounter = 0;
        this.panels = new HashMap<>();
        AnkorPatterns.initViewModel(this, myRef);
    }

    @ActionListener
    public void createAnimalSearchPanel() {
        String panelId = createNextPanelId();
        Ref panelsRef = myRef.appendPath("panels");
        //Ref panelRef = panelsRef.appendLiteralKey(panelId);  todo: why does this not work?
        Ref panelRef = panelsRef.appendPath(panelId);

        AnimalSearchModel model = new AnimalSearchModel(panelRef.appendPath("model"),
                                                        panelRef.appendPath("name").<String>toTypedRef(),
                                                        serverStatusRef, animalRepository);
        model.reloadAnimals();

        Panel<AnimalSearchModel> panel = new Panel<>(panelId, panelRef, "Animal Search", "animalSearch",
                                                     model);

        panelRef.setValue(panel);

    }

    private String createNextPanelId() {
        return "A" + (++panelIdCounter);  // todo: without "A"
    }

    @ActionListener
    public void createAnimalDetailPanel() {
        String panelId = createNextPanelId();
        Ref panelsRef = myRef.appendPath("panels");
        //Ref panelRef = panelsRef.appendLiteralKey(panelId);
        Ref panelRef = panelsRef.appendPath(panelId);

        AnimalDetailModel model = new AnimalDetailModel(panelRef.appendPath("model"),
                                                        panelRef.appendPath("name").<String>toTypedRef(),
                                                        serverStatusRef,
                                                        animalRepository,
                                                        new Animal());
        Panel<AnimalDetailModel> panel = new Panel<>(panelId, panelRef, "New Animal", "animalDetail",
                                                     model);

        panelRef.setValue(panel);
    }

    public Map<String, Panel> getPanels() {
        return panels;
    }
}
