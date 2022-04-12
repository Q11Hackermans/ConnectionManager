package net.jandie1505.connectionmanager.server;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class CMSInputStream extends InputStream {
    CMSClient owner;
    List<Integer> queue;
    CountDownLatch latch;
    int b;

    public CMSInputStream(CMSClient client) {
        this.owner = client;
        this.queue = new ArrayList<>();
        b = -2;

        new Thread(() -> {
            while(!Thread.currentThread().isInterrupted() && !this.owner.isClosed()) {
                if(this.latch != null && this.latch.getCount() != 0 && this.queue.size() > 0) {
                    this.latch.countDown();
                }
            }
        }).start();
    }

    @Override
    public int read() {
        try {
            this.latch = new CountDownLatch(1);
            this.latch.await();
            return this.queue.remove(0);
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
}
