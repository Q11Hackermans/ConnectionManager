package net.jandie1505.connectionmanager;

import net.jandie1505.connectionmanager.events.CMClientEvent;
import net.jandie1505.connectionmanager.interfaces.ByteSender;
import net.jandie1505.connectionmanager.interfaces.StreamOwner;
import net.jandie1505.connectionmanager.streams.CMConsumingInputStream;
import net.jandie1505.connectionmanager.streams.CMInputStream;
import net.jandie1505.connectionmanager.streams.CMTimedInputStream;
import net.jandie1505.connectionmanager.streams.CMOutputStream;

import java.util.ArrayList;
import java.util.List;

public class CMMultiStreamHandler implements StreamOwner, ByteSender {
    private final CMClient owner;
    private final List<CMInputStream> inputStreams;
    private final List<CMOutputStream> outputStreams;
    private final List<Integer> byteQueue;

    // SETUP
    public CMMultiStreamHandler(CMClient owner) {
        this.owner = owner;
        this.inputStreams = new ArrayList<>();
        this.outputStreams = new ArrayList<>();
        this.byteQueue = new ArrayList<>();

        new Thread(() -> {
            Thread.currentThread().setName(this + "-Thread");
            while(!Thread.currentThread().isInterrupted() && !this.isClosed()) {
                synchronized(byteQueue) {
                    if(byteQueue.size() > 0) {
                        for(CMInputStream inputStream : inputStreams) {
                            inputStream.send(byteQueue.remove(0));
                        }
                    }
                }
            }
        }).start();
    }

    // SEND BYTES TO INPUTSTREAM
    protected void send(int b) {
        synchronized(this.byteQueue) {
            this.byteQueue.add(b);
        }
    }

    // SEND BYTES TO CLIENT
    /**
     * This will send bytes to the Client
     * @param b byte int
     */
    @Override
    public void sendByte(int b) {
        this.owner.sendByte(b);
    }

    // MANAGE INPUT STREAMS
    /**
     * Create a new InputStream
     * @return The created InputStream
     */
    public CMTimedInputStream addTimedInputStream() {
        CMTimedInputStream inputStream = new CMTimedInputStream(this);
        this.inputStreams.add(inputStream);
        return inputStream;
    }

    public CMConsumingInputStream addConsumingInputStream() {
        CMConsumingInputStream inputStream = new CMConsumingInputStream(this);
        this.inputStreams.add(inputStream);
        return inputStream;
    }

    /**
     * Remove an InputStream with a specific index
     * @param index Index
     */
    public void removeInputStream(int index) {
        this.inputStreams.remove(index);
    }

    /**
     * Get a list of all registered InputStreams
     * @return List of all InputStreams
     */
    public List<CMInputStream> getInputStreams() {
        return List.copyOf(this.inputStreams);
    }

    // MANAGE OUTPUT STREAMS
    /**
     * Create a new OutputStream
     * @return The created OutputStream
     */
    public CMOutputStream addOutputStream() {
        CMOutputStream outputStream = new CMOutputStream(this);
        this.outputStreams.add(outputStream);
        return outputStream;
    }

    /**
     * Remove an OutputStream with a specific index
     * @param index Index
     */
    public void removeOutputStream(int index) {
        this.outputStreams.remove(index);
    }

    /**
     * Get a list of all registered OutputStream
     * @return List of all OutputStream
     */
    public List<CMOutputStream> getOutputStreams() {
        return List.copyOf(this.outputStreams);
    }

    // CLOSE
    @Override
    public boolean isClosed() {
        return this.owner.isClosed();
    }

    @Override
    public void fireEvent(CMClientEvent event) {
        this.owner.fireEvent(event);
    }

    @Override
    public CMClient getEventClient() {
        return this.owner;
    }
}
