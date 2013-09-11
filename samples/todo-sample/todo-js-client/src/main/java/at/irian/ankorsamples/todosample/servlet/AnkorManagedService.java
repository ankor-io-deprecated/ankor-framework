package at.irian.ankorsamples.todosample.servlet;

/*
// @ManagedService(path = "/ankor")
public class AnkorManagedService {
    private final Logger logger = LoggerFactory.getLogger(AnkorManagedService.class);

    private AnkorSystem ankorSystem;
    private AtmosphereMessageBus atmosphereMessageBus;

    public AnkorManagedService() {
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

    @Ready
    public void onReady(final AtmosphereResource r) {
        logger.info("Browser {} connected.", r.uuid());
        atmosphereMessageBus.addRemoteSystem(new AtmosphereRemoteSystem(r));
    }

    @Disconnect
    public void onDisconnect(AtmosphereResourceEvent event) {
        // if (event.isCancelled()) { } else if (event.isClosedByClient()) { }
        atmosphereMessageBus.removeRemoteSystem(event.getResource().uuid());
    }

    @Post
    public void onMessage(AtmosphereResource resource, String s) {
        atmosphereMessageBus.receiveSerializedMessage(s);
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
