package at.irian.ankorsamples.todosample.servlet;

import at.irian.ankor.session.RemoteSystem;
import org.atmosphere.cpr.AtmosphereResource;

public class AtmosphereRemoteSystem implements RemoteSystem {

    private String id;
    private AtmosphereResource resource;

    public AtmosphereRemoteSystem(AtmosphereResource resource) {
        this.id = resource.uuid();
        this.resource = resource;
    }

    @Override
    public String getId() {
        return id;
    }

    public AtmosphereResource getResource() {
        return resource;
    }

    public void setResource(AtmosphereResource resource) {
        this.resource = resource;
    }
}
