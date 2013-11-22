package at.irian.ankorsamples.animals.viewmodel;

import at.irian.ankor.annotation.ActionListener;
import at.irian.ankor.annotation.Param;
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
    private final Ref resourcesRef;

    @AnkorIgnore private final AnimalRepository animalRepository;

    @AnkorIgnore private int panelIdCounter;
    private final Map<String, Panel> panels;

    protected ContentPane(Ref contentPaneRef,
                          TypedRef<String> serverStatusRef,
                          Ref resourcesRef, AnimalRepository animalRepository) {
        this.myRef = contentPaneRef;
        this.serverStatusRef = serverStatusRef;
        this.resourcesRef = resourcesRef;
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
                                                        serverStatusRef, resourcesRef, animalRepository);
        model.reloadAnimals();

        Panel<AnimalSearchModel> panel = new Panel<>(panelId, panelRef, model.getPanelName(), "animalSearch",
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
        Ref panelRef = panelsRef.appendPath(panelId);

        AnimalDetailModel model = new AnimalDetailModel(panelRef.appendPath("model"),
                                                        panelRef.appendPath("name").<String>toTypedRef(),
                                                        serverStatusRef,
                                                        animalRepository,
                                                        resourcesRef, new Animal());
        Panel<AnimalDetailModel> panel = new Panel<>(panelId, panelRef, model.getPanelName(), "animalDetail",
                                                     model);

        panelRef.setValue(panel);
    }

    @ActionListener(name = "edit", pattern = "**.<AnimalSearchModel>")
    public void createAnimalDetailPanel(@Param("uuid") String uuid) {
        String panelId = createNextPanelId();
        Ref panelsRef = myRef.appendPath("panels");
        Ref panelRef = panelsRef.appendPath(panelId);

        Animal animal = animalRepository.findAnimal(uuid);
        AnimalDetailModel model = new AnimalDetailModel(panelRef.appendPath("model"),
                                                        panelRef.appendPath("name").<String>toTypedRef(),
                                                        serverStatusRef,
                                                        animalRepository,
                                                        resourcesRef, animal);
        Panel<AnimalDetailModel> panel = new Panel<>(panelId, panelRef, model.getPanelName(), "animalDetail",
                                                     model);

        panelRef.setValue(panel);
    }

    public Map<String, Panel> getPanels() {
        return panels;
    }
}
