package net.jandie1505.connectionmanager.streams;

import net.jandie1505.connectionmanager.interfaces.ThreadStopCondition;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class CMInputStream extends InputStream {
    private ThreadStopCondition owner;
    private List<Integer> queue;
    private CountDownLatch latch;
    private Thread thread;
    private int value;
    private long time;

    public CMInputStream(ThreadStopCondition client) {
        this.latch = new CountDownLatch(1);
        this.owner = client;
        this.queue = new ArrayList<>();
        this.value = -2;
        this.time = 10;

        this.thread = new Thread(() -> {
            while(!Thread.currentThread().isInterrupted() && !this.owner.isClosed()) {
                if(this.latch != null && this.latch.getCount() != 0 && this.queue.size() > 0) {
                    this.value = this.queue.remove(0);
                    this.latch.countDown();
                    this.latch = new CountDownLatch(1);
                    try {
                        Thread.sleep(this.time);
                    } catch (InterruptedException ignored) {
                        //
                    }
                }
            }
            this.value = -1;
            if(this.latch != null) {
                this.latch.countDown();
            }
        });
        this.thread.setName(this + "-Thread");
        this.thread.start();
    }

    /**
     * Set the time interval for sending the next byte to all threads that called the read() method.
     * @param millis Time in milliseconds
     */
    public void setTimeInterval(long millis) {
        this.time = millis;
    }

    /**
     * This method is blocking the current thread until a byte is received.
     * @return byte int
     */
    @Override
    public int read() {
        try {
            if(!this.thread.isInterrupted() && !this.owner.isClosed()) {
                this.latch.await();
                return value;
            } else {
                return -1;
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * DO NOT USE THIS!
     * @param b byte int (0-255)
     * @deprecated DO NOT USE THIS!
     */
    @Deprecated
    public void send(int b) {
        if(b >= 0 && this.latch != null) {
            this.queue.add(b);
        }
    }

    @Override
    public void close() {
        this.thread.interrupt();
    }
}
