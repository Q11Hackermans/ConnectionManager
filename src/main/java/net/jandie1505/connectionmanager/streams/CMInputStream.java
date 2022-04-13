package net.jandie1505.connectionmanager.streams;

import net.jandie1505.connectionmanager.CMClient;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class CMInputStream extends InputStream {
    private CMClient owner;
    private List<Integer> queue;
    private CountDownLatch latch;
    private Thread thread;

    public CMInputStream(CMClient client) {
        this.owner = client;
        this.queue = new ArrayList<>();

        this.thread = new Thread(() -> {
            while(!Thread.currentThread().isInterrupted() && !this.owner.isClosed()) {
                if(this.latch != null && this.latch.getCount() != 0 && this.queue.size() > 0) {
                    this.latch.countDown();
                }
            }
        });
        this.thread.start();
    }

    @Override
    public int read() {
        try {
            this.latch = new CountDownLatch(1);
            if(!this.thread.isInterrupted()) {
                this.latch.await();
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
