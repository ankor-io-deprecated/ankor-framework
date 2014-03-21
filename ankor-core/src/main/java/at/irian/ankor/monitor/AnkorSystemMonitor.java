package at.irian.ankor.monitor;

/**
 * @author Manfred Geiler
 */
public interface AnkorSystemMonitor {

    SwitchboardMonitor switchboard();

    ModelSessionMonitor modelSession();

    //RoutingTableMonitor routingTable();
}
