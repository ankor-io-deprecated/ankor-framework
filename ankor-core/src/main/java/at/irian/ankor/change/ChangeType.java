package at.irian.ankor.change;

/**
 * @author Manfred Geiler
 */
public enum ChangeType {
    value,
    insert,
    delete,     //todo  distinguish between array/list remove and map remove
    replace
}
