package at.irian.ankorsamples.preformance;

import com.fasterxml.jackson.databind.ObjectMapper;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@ServerEndpoint("/master")
public class Master {
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(Master.class);
    private static Set<Session> minions = Collections.newSetFromMap(new ConcurrentHashMap<Session, Boolean>(0, 0.9f, 1));
    private static ObjectMapper mapper = new ObjectMapper();

    public static void startTest(int n) {
        TestJSON test = new TestJSON();
        test.setN(n);

        try {
            String s = mapper.writeValueAsString(test);
            for (Session minion : minions) {
                if (minion.isOpen()) {
                    minion.getBasicRemote().sendText(s);
                } else {
                    minions.remove(minion);
                }
            }
            LOG.info("Sending test data to {} connected clients", minions.size());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @OnOpen
    public void onOpen(Session session) {
        minions.add(session);
        int currentMinions = minions.size();
        LOG.info("Current minions: {}", currentMinions);
    }

    @OnMessage
    public void onMessage(Session session, String msg) {
        try {
            if (msg.contains("report")) {
                ReportJSON report = mapper.readValue(msg, ReportJSON.class);
                LOG.info("Received report: Avg: {}ms, Std: {}ms, Failures: {}",
                        report.getAvg(),
                        report.getStd(),
                        report.getFailures());
            } else if (msg.contains("test")) {
                int n = Integer.parseInt(msg.split(" ")[1]);
                startTest(n);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @OnClose
    public void onClose(Session session, CloseReason closeReason) {
        minions.remove(session);
        int currentMinions = minions.size();
        LOG.info("Current minions: {}", currentMinions);
    }

}
