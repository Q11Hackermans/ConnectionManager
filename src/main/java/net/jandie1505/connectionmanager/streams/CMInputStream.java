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

    public CMInputStream(ThreadStopCondition client) {
        this.owner = client;
        this.queue = new ArrayList<>();

        this.thread = new Thread(() -> {
            while(!Thread.currentThread().isInterrupted() && !this.owner.isClosed()) {
                if(this.latch != null && this.latch.getCount() != 0 && this.queue.size() > 0) {
                    this.latch.countDown();
                    try {
                        Thread.sleep(1);
                        this.queue.remove(0);
                    } catch (InterruptedException ignored) {
                        //
                    }
                }
            }
        });
        this.thread.start();
    }

    @Override
    public int read() {
        try {
            if(this.latch == null) {
                this.latch = new CountDownLatch(1);
            }
            if(!this.thread.isInterrupted()) {
                this.latch.await();
                this.latch = null;
                return this.queue.remove(0);
            } else {
                return -1;
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return -1;
    }

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
