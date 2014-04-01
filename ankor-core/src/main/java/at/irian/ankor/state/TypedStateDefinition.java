package at.irian.ankor.state;

/**
 * @author Manfred Geiler
 */
public interface TypedStateDefinition extends StateDefinition {

    Class<?> getTypeOf(String path);

}
