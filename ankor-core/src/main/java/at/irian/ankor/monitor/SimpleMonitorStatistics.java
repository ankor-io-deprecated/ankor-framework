package at.irian.ankor.monitor;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Thomas Spiegl
 */
@Deprecated
public class SimpleMonitorStatistics {

    private final long traceInterval;
    private final IntTimeSeries connections;
    private final IntTimeSeries modelSessions;
    private final Map<Object, IntTimeSeries> inboundMessages;
    private final Map<Object, IntTimeSeries> outboundMessages;

    public SimpleMonitorStatistics(long traceInterval) {
        this.traceInterval = traceInterval;
        this.connections = new IntTimeSeries(traceInterval);
        this.modelSessions = new IntTimeSeries(traceInterval);
        this.inboundMessages = new HashMap<Object, IntTimeSeries>();
        this.outboundMessages = new HashMap<Object, IntTimeSeries>();
    }

    public IntTimeSeries getConnections() {
        return connections;
    }

    public IntTimeSeries getModelSessions() {
        return modelSessions;
    }

    public IntTimeSeries getInbound(Object connector) {
        IntTimeSeries intTimeSeries = inboundMessages.get(connector);
        if (intTimeSeries == null) {
            intTimeSeries = new IntTimeSeries(traceInterval);
            inboundMessages.put(connector, intTimeSeries);
        }
        return intTimeSeries;
    }

    public IntTimeSeries getOutbound(Object connector) {
        IntTimeSeries intTimeSeries = outboundMessages.get(connector);
        if (intTimeSeries == null) {
            intTimeSeries = new IntTimeSeries(traceInterval);
            outboundMessages.put(connector, intTimeSeries);
        }
        return intTimeSeries;
    }

    @SuppressWarnings("UnusedDeclaration")
    public Map<Object, IntTimeSeries> getInboundMessages() {
        return inboundMessages;
    }

    @SuppressWarnings("UnusedDeclaration")
    public Map<Object, IntTimeSeries> getOutboundMessages() {
        return outboundMessages;
    }

    public void resetTo(long time) {
        connections.resetTo(time);
        modelSessions.resetTo(time);
        for (IntTimeSeries intTimeSeries : inboundMessages.values()) {
            intTimeSeries.resetTo(time);
        }
        for (IntTimeSeries intTimeSeries : outboundMessages.values()) {
            intTimeSeries.resetTo(time);
        }
    }

    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder();
        for (Map.Entry<Object, IntTimeSeries> entry : inboundMessages.entrySet()) {
            buf.append(entry.getKey()).append("_in=")
                    .append(entry.getValue().getTotalValue());
        }
        for (Map.Entry<Object, IntTimeSeries> entry : outboundMessages.entrySet()) {
            buf.append(", ").append(entry.getKey()).append("_out=")
                    .append(entry.getValue().getTotalValue());
        }
        return String.format(
                "SimpleMonitorStatistics{connections=%d, modelSessions=%d, %s}",
                connections.getTotalValue(),
                modelSessions.getTotalValue(),
                buf.toString());
    }
}
