package at.irian.ankor.ref.match;

/**
 * @author Manfred Geiler
 */
public interface RefMatcherFactory {
    RefMatcher getRefMatcher(String pattern);
}
