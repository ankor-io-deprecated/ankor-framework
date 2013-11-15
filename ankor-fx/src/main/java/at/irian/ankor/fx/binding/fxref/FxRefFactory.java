package at.irian.ankor.fx.binding.fxref;

import at.irian.ankor.ref.RefFactory;

/**
 * @author Manfred Geiler
 */
public interface FxRefFactory extends RefFactory {
    FxRef ref(String path);
}
