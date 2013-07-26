package at.irian.ankor.ref;

import at.irian.ankor.path.PathSyntax;

/**
 * @author Manfred Geiler
 */
public interface RefContextFactory {

    PathSyntax getPathSyntax();

    RefContext createRefContext();

}
