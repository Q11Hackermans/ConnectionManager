package net.jandie1505.connectionmanager.utilities.dataiostreamhandler;

import net.jandie1505.connectionmanager.CMClient;
import net.jandie1505.connectionmanager.streams.CMTimedInputStream;
import net.jandie1505.connectionmanager.streams.CMOutputStream;
import net.jandie1505.connectionmanager.utilities.dataiostreamhandler.events.*;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class DataIOStreamHandler {
    private CMClient client;
    private InputStream inputStream;
    private OutputStream outputStream;
    private boolean opened;
    private final List<DataIOEventListener> listeners;
    private final DataIOType listeningType;
    private Thread thread;
    private Thread eventQueueThread;
    private final List<DataIOEvent> eventQueue;
    private final DataIOStreamType inputStreamType;

    public DataIOStreamHandler(CMClient client, DataIOType type, DataIOStreamType inputStreamType) {
        this.client = client;
        this.listeners = new ArrayList<>();
        this.listeningType = type;
        this.eventQueue = new ArrayList<>();
        this.opened = true;
        this.inputStreamType = inputStreamType;

        if(inputStreamType == DataIOStreamType.MULTI_STREAM_HANDLER_TIMED || inputStreamType == DataIOStreamType.MULTI_STREAM_HANDLER_CONSUMING) {
            if(client.getMultiStreamHandler() == null) {
                this.client.enableMultiStreamHandler();
            }
            if(inputStreamType == DataIOStreamType.MULTI_STREAM_HANDLER_CONSUMING) {
                this.inputStream = this.client.getMultiStreamHandler().addConsumingInputStream();
            } else {
                this.inputStream = this.client.getMultiStreamHandler().addTimedInputStream();
            }
            this.outputStream = this.client.getMultiStreamHandler().addOutputStream();
        } else {
            this.inputStream = client.getInputStream();
            this.outputStream = client.getOutputStream();
        }

        this.thread = new Thread(() -> {
            while(!Thread.currentThread().isInterrupted() && opened && !client.isClosed()) {
                try {
                    DataInputStream in = new DataInputStream(inputStream);
                    if(listeningType == DataIOType.BYTE) {
                        this.fireEvent(new DataIOByteReceivedEvent(this, client, in.readByte()));
                    } else if(listeningType == DataIOType.BOOLEAN) {
                        this.fireEvent(new DataIOBooleanReceivedEvent(this, client, in.readBoolean()));
                    } else if(listeningType == DataIOType.SHORT) {
                        this.fireEvent(new DataIOShortReceivedEvent(this, client, in.readShort()));
                    } else if(listeningType == DataIOType.CHAR) {
                        this.fireEvent(new DataIOCharReceivedEvent(this, client, in.readChar()));
                    } else if(listeningType == DataIOType.INT) {
                        this.fireEvent(new DataIOIntReceivedEvent(this, client, in.readInt()));
                    } else if(listeningType == DataIOType.LONG) {
                        this.fireEvent(new DataIOLongReceivedEvent(this, client, in.readChar()));
                    } else if(listeningType == DataIOType.FLOAT) {
                        this.fireEvent(new DataIOFloatReceivedEvent(this, client, in.readFloat()));
                    } else if(listeningType == DataIOType.DOUBLE) {
                        this.fireEvent(new DataIODoubleReceivedEvent(this, client, in.readDouble()));
                    } else if(listeningType == DataIOType.UTF) {
                        this.fireEvent(new DataIOUTFReceivedEvent(this, client, in.readUTF()));
                    }
                } catch(IOException e) {
                    Thread.currentThread().interrupt();
                    close();
                    e.printStackTrace();
                }
            }
        });
        this.thread.setName(this + "-ListenerThread");
        this.thread.start();

        this.eventQueueThread = new Thread(() -> {
            while(!Thread.currentThread().isInterrupted() && opened && !client.isClosed()) {
                if(eventQueue.size() > 0) {
                    for(DataIOEventListener listener : listeners) {
                        try {
                            listener.onEvent(eventQueue.get(0));
                        } catch(Exception e) {
                            e.printStackTrace();
                        }
                    }
                    eventQueue.remove(0);
                }
            }
        });
        this.eventQueueThread.setName(this + "-EventQueueThread");
        this.eventQueueThread.start();
    }

    // SEND

    public synchronized void writeBoolean(boolean v) throws IOException {
        DataOutputStream out = new DataOutputStream(this.outputStream);
        out.writeBoolean(v);
    }

    public synchronized void writeShort(short v) throws IOException {
        DataOutputStream out = new DataOutputStream(this.outputStream);
        out.writeShort(v);
    }

    public synchronized void writeChar(char v) throws IOException {
        DataOutputStream out = new DataOutputStream(this.outputStream);
        out.writeChar(v);
    }

    public synchronized void writeInt(int v) throws IOException {
        DataOutputStream out = new DataOutputStream(this.outputStream);
        out.writeInt(v);
    }

    public synchronized void writeLong(long v) throws IOException {
        DataOutputStream out = new DataOutputStream(this.outputStream);
        out.writeLong(v);
    }

    public synchronized void writeFloat(float v) throws IOException {
        DataOutputStream out = new DataOutputStream(this.outputStream);
        out.writeFloat(v);
    }

    public synchronized void writeDouble(double v) throws IOException {
        DataOutputStream out = new DataOutputStream(this.outputStream);
        out.writeDouble(v);
    }

    public synchronized void writeUTF(String str) throws IOException {
        DataOutputStream out = new DataOutputStream(this.outputStream);
        out.writeUTF(str);
    }

    // EVENT LISTENERS

    public void addEventListener(DataIOEventListener listener) {
        if(!this.listeners.contains(listener)) {
            this.listeners.add(listener);
        }
    }

    public void removeEventListener(int index) {
        this.listeners.remove(index);
    }

    public List<DataIOEventListener> getEventListeners() {
        return List.copyOf(this.listeners);
    }

    protected void fireEvent(DataIOEvent event) {
        eventQueue.add(event);
    }

    // CLIENT

    /**
     * Returns the client
     * @return CMClient
     */
    public CMClient getClient() {
        return this.client;
    }

    // CLOSE

    /**
     * Shutdown threads and close DataIOStreamHandler
     */
    public void close() {
        this.thread.interrupt();
        this.eventQueueThread.interrupt();
        try {
            this.thread.stop();
        } catch(Exception e) {
            e.printStackTrace();
        }
        try {
            this.eventQueueThread.stop();
        } catch(Exception e) {
            e.printStackTrace();
        }
        this.opened = false;
        if(this.client.getMultiStreamHandler() != null) {
            if(this.client.getMultiStreamHandler().getInputStreams().contains((CMTimedInputStream) this.inputStream)) {
                this.client.getMultiStreamHandler().removeInputStream(this.client.getMultiStreamHandler().getInputStreams().indexOf((CMTimedInputStream) this.inputStream));
            }
            if(this.client.getMultiStreamHandler().getOutputStreams().contains((CMOutputStream) this.outputStream)) {
                this.client.getMultiStreamHandler().removeOutputStream(this.client.getMultiStreamHandler().getOutputStreams().indexOf((CMOutputStream) this.outputStream));
            }
        }
        this.client = null;
        this.inputStream = null;
        this.outputStream = null;
    }

    public boolean isClosed() {
        return !this.opened;
    }
}
