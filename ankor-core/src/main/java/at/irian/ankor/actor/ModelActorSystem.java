package at.irian.ankor.actor;

/**
 * @author Manfred Geiler
 */
public interface ModelActorSystem {

    ModelActorRef findModelActorByModelId(String modelId);

    ModelActorRef createModelActor(String modelId);

}
