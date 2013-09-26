package at.irian.ankorsamples.animals.websocket;

import at.irian.ankor.ref.Ref;
import at.irian.ankor.servlet.websocket.AnkorEndpoint;
import at.irian.ankorsamples.animals.domain.animal.AnimalRepository;
import at.irian.ankorsamples.animals.viewmodel.ModelRoot;

public class AnimalsSampleEndpoint extends AnkorEndpoint {

    @Override
    protected String getName() {
        return "animals-sample-servlet-server";
    }

    @Override
    protected Object getModelRoot(Ref rootRef) {
        return new ModelRoot(rootRef, new AnimalRepository());
    }

    @Override
    protected String getWebSocketUrl() {
        return "/ankor";
    }
}
