package at.irian.ankorsamples.todosample.servlet;

/*
@WebSocketHandlerService(path = "/ankor", broadcaster = SimpleBroadcaster.class)
public class AnkorWebSocketHandler extends WebSocketHandlerAdapter {

    private AnkorSystem ankorSystem;
    private AtmosphereMessageBus atmosphereMessageBus;

    public AnkorWebSocketHandler() {
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
    public void onOpen(WebSocket webSocket) throws IOException {
        atmosphereMessageBus.addRemoteSystem(new AtmosphereRemoteSystem(webSocket.resource()));

        webSocket.resource().addEventListener(new WebSocketEventListenerAdapter() {
            @Override
            public void onDisconnect(AtmosphereResourceEvent event) {
                // if (event.isCancelled()) { } else if (event.isClosedByClient()) { }
                atmosphereMessageBus.removeRemoteSystem(event.getResource().uuid());
            }
        });
    }

    @Override
    public void onTextMessage(WebSocket webSocket, String message) throws IOException {
        atmosphereMessageBus.receiveSerializedMessage(message);
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
*/
