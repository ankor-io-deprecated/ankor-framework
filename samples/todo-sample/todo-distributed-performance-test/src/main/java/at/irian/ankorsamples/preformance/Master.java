package at.irian.ankorsamples.preformance;

import com.fasterxml.jackson.databind.ObjectMapper;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@ServerEndpoint("/master")
public class Master {
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(Master.class);
    private static final long TIMEOUT = 30000;
    private static final long TIMEOUT_CHECK_INTERVAL = 30000;
    private static Map<Session, Long> minions = new ConcurrentHashMap<>(0, 0.9f, 1);
    private static ObjectMapper mapper = new ObjectMapper();
    private static Map<Session, Report> reports;
    private static int numClientsPerMinion;
    private static int numMinions;
    private static ScheduledExecutorService timer = Executors.newSingleThreadScheduledExecutor();

    static {
        timer.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                watchForTimeout();
            }
        }, TIMEOUT_CHECK_INTERVAL, TIMEOUT_CHECK_INTERVAL, TimeUnit.MILLISECONDS);
    }

    public static void startTest(WorkLoad workLoad) {
        try {
            String s = mapper.writeValueAsString(workLoad);
            for (Session minion : minions.keySet()) {
                if (minion.isOpen()) {
                    minion.getBasicRemote().sendText(s);
                } else {
                    minions.remove(minion);
                }
            }

            numClientsPerMinion = workLoad.getN();
            numMinions = minions.size();

            LOG.info("Sending test data to {} connected clients", numMinions);
            reports = new ConcurrentHashMap<>(numMinions, 0.9f, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void sendPing() {
        for (Session session : minions.keySet()) {
            if (session.isOpen()) {
                String data = "You There?";
                ByteBuffer payload = ByteBuffer.wrap(data.getBytes());
                try {
                    session.getBasicRemote().sendPing(payload);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static double average(List<Integer> responseTimes, int n) {
        double sum = 0;
        for (int time : responseTimes) {
            sum += time;
        }
        return sum / n;
    }

    private static double variance(List<Integer> responseTimes, int n, double avg) {
        if (n == 1) return 0;

        double sum = 0;
        double term;
        for (int time : responseTimes) {
            term = time - avg;
            sum += term * term; // ^2
        }
        return sum / (n - 1);
    }

    private static void watchForTimeout() {
        timer.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                for (Session session : minions.keySet()) {
                    long now = System.currentTimeMillis();
                    long lastSeen = minions.get(session);
                    if (now - lastSeen > TIMEOUT) {
                        if (session.isOpen()) {
                            try {
                                session.close(new CloseReason(CloseReason.CloseCodes.GOING_AWAY, "Timeout"));
                            } catch (IOException e) {
                                LOG.error("Error while closing the connection after timeout");
                            }
                        }
                    }
                }
            }
        }, TIMEOUT_CHECK_INTERVAL, TIMEOUT_CHECK_INTERVAL, TimeUnit.MILLISECONDS);
    }

    @OnOpen
    public void onOpen(Session session) {
        minions.put(session, System.currentTimeMillis());
        int currentMinions = minions.size();
        LOG.info("Current minions: {}", currentMinions);
        broadcastNumConnected(session);
    }

    private void broadcastNumConnected(Session session) {
        for (Session s : session.getOpenSessions()) {
            s.getAsyncRemote().sendText(String.format("{ \"numClients\": %d }", minions.size()));
        }
    }

    @OnMessage
    public void onMessage(Session session, String msg) {
        try {
            if (msg.contains("Report")) {
                Report report = mapper.readValue(msg, Report.class);
                reportStats(report);

                reports.put(session, report);
                if (reports.size() == numMinions) {
                    reportOverallStats();
                }
            } else if (msg.contains("WorkLoad")) {
                WorkLoad workLoad = mapper.readValue(msg, WorkLoad.class);
                startTest(workLoad);
            } else if (isHeartbeat(msg)) {
                minions.put(session, System.currentTimeMillis());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean isHeartbeat(String msg) {
        return msg.trim().equals("");
    }

    private void reportStats(Report report) {
        LOG.debug("Received report: Avg: {}ms, Std: {}ms, Failures: {}, Response times: {}",
                report.getAvg(),
                report.getStd(),
                report.getFailures(),
                report.getResponseTimes());
    }

    private void reportOverallStats() {
        List<Integer> responseTimes = new ArrayList<>();
        int failures = 0;
        for (Report r : reports.values()) {
            for (int responseTime : r.getResponseTimes()) {
                responseTimes.add(responseTime);
            }
            failures += r.getFailures();
        }

        double avg = average(responseTimes, responseTimes.size());
        double std = Math.sqrt(variance(responseTimes, responseTimes.size(), avg));

        Collections.sort(responseTimes);
        int quartile90 = responseTimes.get((int) (responseTimes.size() * 0.9));
        int max = responseTimes.get(responseTimes.size() - 1);

        LOG.info("OverallReport report from {} clients ({} simulated): Avg: {}ms, Std: {}ms, 90% of requests are below: {}ms, Max: {}ms, Failures: {}",
                numMinions, numClientsPerMinion * numMinions, avg, std, quartile90, max, failures);

        OverallReport report = new OverallReport();
        report.setNumClients(numMinions);
        report.setNumSimulatedClients(numClientsPerMinion * numMinions);
        report.setAvg(avg);
        report.setStd(std);
        report.setFailures(failures);
        report.setQuartile90(quartile90);
        report.setMax(max);

        try {
            String s = mapper.writeValueAsString(report);
            for (Session session : minions.keySet()) {
                session.getAsyncRemote().sendText(s);
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
        broadcastNumConnected(session);
    }

}
