package at.irian.ankorsamples.animals.server;

import at.irian.ankor.system.AnkorSystemBuilder;

/**
 * @author Manfred Geiler
 */
public class SocketServerStarter {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(SocketServerStarter.class);

    public static void main(String[] args) {
        new SocketServerStarter().start();
    }

    public void start() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                new AnkorSystemBuilder()
                        .withApplication(new AnimalsServerApplication())
                        .createServer()
                        .start();
            }
        });
        thread.setDaemon(false);
        thread.setName("Animals main server thread");
        thread.start();

        sleepForever();
    }

    private void sleepForever() {
        boolean interrupted = false;
        while (!interrupted) {
            try {
                Thread.sleep(Long.MAX_VALUE);
            } catch (InterruptedException e) {
                interrupted = true;
            }
        }
        Thread.currentThread().interrupt();
    }

}
