package net.jandie1505.connectionmanager.streams;

import net.jandie1505.connectionmanager.events.CMClientInputStreamByteLimitReachedEvent;
import net.jandie1505.connectionmanager.interfaces.StreamOwner;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class CMTimedInputStream extends CMInputStream {
    private final List<Integer> queue;
    private final Thread thread;
    private CountDownLatch latch;
    private int value;
    private long time;
    private int streamByteLimit;

    public CMTimedInputStream(StreamOwner client) {
        super(client);
        this.latch = new CountDownLatch(1);
        this.queue = Collections.synchronizedList(new ArrayList<>());
        this.value = -2;
        this.time = 10;
        this.streamByteLimit = 2500000;

        this.thread = new Thread(() -> {
            while(!Thread.currentThread().isInterrupted() && !this.getOwner().isClosed()) {
                synchronized(this.queue) {
                    if(this.latch != null && this.latch.getCount() != 0 && this.queue.size() > 0) {
                        if(!(this.queue.size() > this.streamByteLimit)) {
                            this.value = this.queue.remove(0);
                            this.latch.countDown();
                            this.latch = new CountDownLatch(1);
                        } else {
                            this.queue.clear();
                            this.getOwner().fireEvent(new CMClientInputStreamByteLimitReachedEvent(this.getOwner().getEventClient(), this));
                        }
                    } else {
                        this.value = -2;
                    }
                }
                try {
                    Thread.sleep(this.time);
                } catch(InterruptedException ignored) {
                    // IGNORED
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
            if(!this.thread.isInterrupted() && !this.getOwner().isClosed()) {
                while(this.latch.getCount() == 0) {
                    Thread.sleep(1);
                }
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
    @Override
    @Deprecated
    public void send(int b) {
        if(b >= 0 && this.latch != null) {
            synchronized(this.queue) {
                this.queue.add(b);
            }
        }
    }

    @Override
    public void close() {
        this.thread.interrupt();
    }

    public int getStreamByteLimit() {
        return streamByteLimit;
    }

    public void setStreamByteLimit(int streamByteLimit) {
        if(this.streamByteLimit > 0) {
            this.streamByteLimit = streamByteLimit;
        } else {
            throw new IllegalArgumentException("The stream discard amount must be positive");
        }
    }
}
