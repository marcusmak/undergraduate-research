package hk.ust.mtrec.multisensorcollector.persistence;

import java.io.IOException;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * Created by tanjiajie on 2/9/17.
 */
public class Persistencer {

    private static final int QUEUE_SIZE = 1024;
    private static final int QUEUE_OFFER_TIMEOUT = 1;   // 1s

    private BlockingQueue<Persistable> queue = new ArrayBlockingQueue<>(QUEUE_SIZE);
    private PersistenceWorker worker;
    private Thread workThread;
    private boolean enabled = true;

    private Map<Integer, PersistenceHandler> handlerMap;

    Persistencer(String taskName, Map<Integer, PersistenceHandler> handlerMap) {
        this.worker = new PersistenceWorker();
        this.handlerMap = handlerMap;
        this.workThread = new Thread(worker, "persistence_worker");
        workThread.start();
    }

    public void put(final Persistable persistable) {
        if (enabled) {
            try {
                queue.offer(persistable, QUEUE_OFFER_TIMEOUT, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void close() {
        workThread.interrupt();
        for (PersistenceHandler persistHandler : handlerMap.values()) {
            try {
                persistHandler.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    private PersistenceHandler pickHandler(int type) {
        return handlerMap.get(type);
    }

    class PersistenceWorker implements Runnable {

        @Override
        public void run() {
            try {
                while (true) {
                    final Persistable persistable = queue.take();
                    PersistenceHandler handler = pickHandler(persistable.getType());
                    if (handler != null) {
                        try {
                            handler.handle(persistable);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }
}
