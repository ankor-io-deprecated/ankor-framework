package at.irian.ankor.path;

/**
 * @author Manfred Geiler
 */
public interface PathSyntax {

    String parentOf(String path);

    String concat(String path, String subPath);

    String addArrayIdx(String path, int index);

    String addMapKey(String path, String key);
}
