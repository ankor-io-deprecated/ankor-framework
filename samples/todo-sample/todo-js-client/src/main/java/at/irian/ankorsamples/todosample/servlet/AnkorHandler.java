package at.irian.ankorsamples.todosample.servlet;

import at.irian.ankor.akka.AnkorActorSystem;
import at.irian.ankor.base.BeanResolver;
import at.irian.ankor.delay.AkkaScheduler;
import at.irian.ankor.event.dispatch.AkkaEventDispatcherFactory;
import at.irian.ankor.messaging.json.viewmodel.ViewModelJsonMessageMapper;
import at.irian.ankor.ref.Ref;
import at.irian.ankor.session.ModelRootFactory;
import at.irian.ankor.system.AnkorSystem;
import at.irian.ankor.system.AnkorSystemBuilder;
import at.irian.ankorsamples.todosample.domain.task.TaskRepository;
import at.irian.ankorsamples.todosample.viewmodel.ModelRoot;
import org.atmosphere.config.service.AtmosphereHandlerService;
import org.atmosphere.cpr.AtmosphereRequest;
import org.atmosphere.cpr.AtmosphereResource;
import org.atmosphere.cpr.AtmosphereResourceEvent;
import org.atmosphere.cpr.AtmosphereResourceEventListenerAdapter;
import org.atmosphere.handler.AtmosphereHandlerAdapter;
import org.atmosphere.websocket.WebSocketEventListenerAdapter;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;

@AtmosphereHandlerService(path = "/ankor")
public class AnkorHandler extends AtmosphereHandlerAdapter {

    private AnkorSystem ankorSystem;
    private AtmosphereMessageBus atmosphereMessageBus;

    public AnkorHandler() {
        AnkorActorSystem ankorActorSystem;
        ankorSystem = new AnkorSystemBuilder()
                .withName(getName())
                .withBeanResolver(getBeanResolver())
                .withModelRootFactory(getModelRootFactory())
                .withMessageBus((atmosphereMessageBus = new AtmosphereMessageBus(new ViewModelJsonMessageMapper())))
                .withDispatcherFactory(new AkkaEventDispatcherFactory((ankorActorSystem = AnkorActorSystem.create())))
                .withScheduler(new AkkaScheduler(ankorActorSystem))
                .createServer();

        ankorSystem.start();
    }

    @Override
    public void onRequest(final AtmosphereResource resource) throws IOException {
        AtmosphereRequest req = resource.getRequest();

        if (req.getMethod().equalsIgnoreCase("GET")) {

            atmosphereMessageBus.addRemoteSystem(new AtmosphereRemoteSystem(resource));

            resource.addEventListener(new WebSocketEventListenerAdapter() {
                @Override
                public void onDisconnect(AtmosphereResourceEvent event) {
                    // if (event.isCancelled()) { } else if (event.isClosedByClient()) { }
                    atmosphereMessageBus.removeRemoteSystem(event.getResource().uuid());
                }
            });

            switch (resource.transport()) {
                case JSONP:
                case AJAX:
                case LONG_POLLING:
                    resource.resumeOnBroadcast(true);
                    break;
                default:
                    break;
            }

            resource.addEventListener(new AtmosphereResourceEventListenerAdapter() {
                @Override
                public void onBroadcast(AtmosphereResourceEvent event) {
                    switch (resource.transport()) {
                        case JSONP:
                        case AJAX:
                        case LONG_POLLING:
                            break;
                        default:
                            try {
                                resource.getResponse().flushBuffer();
                            } catch (IOException ignored) {}
                            break;
                    }
                }
            }).suspend();

        } else if (req.getMethod().equalsIgnoreCase("POST")) {
            resource.getBroadcaster().broadcast(req.getReader().readLine().trim());
        }
    }

    @Override
    public void onStateChange(AtmosphereResourceEvent event) throws IOException {
        if (event.isSuspended()) {
            String message = event.getMessage() == null ? null : event.getMessage().toString();
            if (message != null) {
                atmosphereMessageBus.receiveSerializedMessage(message);
                // event.getResource().write(message.substring("message=".length()));
            }
        }
    }

    protected String getName() {
        return "sample-todo-servlet-server";
    }

    protected BeanResolver getBeanResolver() {
        return new BeanResolver() {
            @Override
            public Object resolveByName(String beanName) {
                return null;
            }

            @Override
            public Collection<String> getKnownBeanNames() {
                return Collections.emptyList();
            }
        };
    }

    protected ModelRootFactory getModelRootFactory() {
        return new ModelRootFactory() {

            @Override
            public Set<String> getKnownRootNames() {
                return Collections.singleton("root");
            }

            @Override
            public Object createModelRoot(Ref rootRef) {
                return new ModelRoot(rootRef, new TaskRepository());
            }
        };
    }
}
