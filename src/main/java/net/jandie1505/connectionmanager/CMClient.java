package net.jandie1505.connectionmanager;

import net.jandie1505.connectionmanager.enums.ClientClosedReason;
import net.jandie1505.connectionmanager.events.*;
import net.jandie1505.connectionmanager.interfaces.ByteSender;
import net.jandie1505.connectionmanager.interfaces.StreamOwner;
import net.jandie1505.connectionmanager.streams.CMTimedInputStream;
import net.jandie1505.connectionmanager.streams.CMOutputStream;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract class CMClient implements StreamOwner, ByteSender, Closeable {
    private final Socket socket;
    private final List<CMClientEventListener> listeners;
    private final List<CMClientEvent> eventQueue;
    private Thread managerThread;
    private Thread eventQueueThread;
    private final CMTimedInputStream inputStream;
    private final CMOutputStream outputStream;
    private CMMultiStreamHandler multiStreamHandler;

    // SETUP
    public CMClient(Socket socket) {
        this(socket, List.of(), (Object) null);
    }

    public CMClient(Socket socket, Collection<CMClientEventListener> listeners) {
        this(socket, listeners, (Object) null);
    }

    public CMClient(Socket socket, Object... constructorParameters) {
        this(socket, List.of(), constructorParameters);
    }

    public CMClient(Socket socket, Collection<CMClientEventListener> listeners, Object... constructorParameters) {
        this.socket = socket;

        this.inputStream = new CMTimedInputStream(this);
        this.outputStream = new CMOutputStream(this);

        this.eventQueue = new ArrayList<>();

        this.listeners = new ArrayList<>();
        this.listeners.addAll(listeners);

        this.setup(constructorParameters);

        this.managerThread = new Thread(() -> {
            while(!Thread.currentThread().isInterrupted() && !socket.isClosed()) {
                try {
                    try {
                        if(stopcondition()) {
                            this.close(ClientClosedReason.STOPCONDITION_TRIGGERED);
                        }
                    } catch(Exception ignored) {}

                    if(!socket.isConnected()) {
                        this.close(ClientClosedReason.CONNECTION_FAILED);
                    }

                    int input = socket.getInputStream().read();
                    if(input == -1) {
                        this.close(ClientClosedReason.DISCONNECTED_BY_REMOTE);
                    } else {
                        this.onByteReceived(input);
                    }
                } catch (IOException e) {
                    this.close(ClientClosedReason.CONNECTION_RESET);
                }

                try {
                    Thread.sleep(1);
                } catch (InterruptedException ignored) {}
            }
        });
        this.managerThread.setName(this + "-ManagerThread");
        this.managerThread.start();

        this.eventQueueThread = new Thread(() -> {
            while(!Thread.currentThread().isInterrupted() && !socket.isClosed()) {
                synchronized(this.eventQueue) {
                    if(eventQueue.size() > 0) {
                        for(CMClientEventListener listener : this.listeners) {
                            try {
                                listener.onEvent(eventQueue.get(0));
                            } catch(Exception e) {
                                e.printStackTrace();
                            }
                        }
                        eventQueue.remove(0);
                    }

                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException ignored) {}
                }
            }
        });
        eventQueueThread.setName(this + "-EventHandlerThread");
        eventQueueThread.start();

        new Thread(() -> {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            this.fireEvent(new CMClientCreatedEvent(this));
        }).start();
    }

    // FOR SUBCLASSES
    /**
     * For setup of subclasses
     * @param constructorParameters Parameters from the constructor
     */
    protected void setup(Object[] constructorParameters) {}

    /**
     * For custom stop conditions of subclasses
     * @return true = stop
     */
    protected boolean stopcondition() {
        return false;
    }

    // EVENT LISTENER
    /**
     * Add an EventListener
     * @param listener CMSClientEventListener
     */
    public void addEventListener(CMClientEventListener listener) {
        this.listeners.add(listener);
    }

    /**
     * Remove an EventListener
     * @param listener CMSClientEventListener
     */
    public void removeEventListener(CMClientEventListener listener) {
        this.listeners.remove(listener);
    }

    /**
     * Get the event listeners
     * @return Listener list
     */
    public List<CMClientEventListener> getListeners() {
        return this.listeners;
    }

    // CLOSING
    /**
     * Close the connection, the Input/Output streams and shutdown the threads
     */
    public void close() {
        try {
            this.eventQueue.add(new CMClientClosedEvent(this, ClientClosedReason.CONNECTION_CLOSED));
        } catch(Exception ignored) {}
        this.close1();
    }

    /**
     * For subclasses
     */
    protected void onClose() {}

    /**
     * Close the connection, the Input/Output streams and shutdown the threads (with a specific reason)
     * @param reason Reason why the client was closed
     */
    public void close(ClientClosedReason reason) {
        try {
            this.fireEvent(new CMClientClosedEvent(this, reason));
        } catch(Exception ignored) {}
        this.close1();
    }

    private void close1() {
        try {
            this.socket.close();
            try {
                this.onClose();
            } catch(Exception e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            this.managerThread.interrupt();
            this.eventQueueThread.interrupt();
            this.inputStream.close();
            System.out.println("Socket error occurred. The client threads were shutdown to avoid error spams. Read StackTrace for more information. Client object: " + this.socket);
            e.printStackTrace();
        }
    }

    public boolean isClosed() {
        return this.socket.isClosed();
    }

    // STREAMS
    /**
     * Get the InputStream of the client.
     * THIS IS NOT THE INPUT STREAM OF THE SOCKET! IT IS A COPY OF IT!
     * USE THIS INPUT STREAM INSTEAD OF THE SOCKET INPUT STREAM TO AVOID ERRORS!
     * @return InputStream
     */
    public CMTimedInputStream getInputStream() {
        return this.inputStream;
    }

    /**
     * Get the OutputStream of the client.
     * THIS IS NOT THE OUTPUT STREAM OF THE SOCKET! IT IS A COPY OF IT!
     * USE THIS OUTPUT STREAM INSTEAD OF THE SOCKET OUTPUT STREAM TO AVOID ERRORS!
     * @return OutputStream
     */
    public CMOutputStream getOutputStream() {
        return this.outputStream;
    }

    // MULTI STREAM MANAGER
    /**
     * Enable the MultiStreamHandler.
     * The MultiStreamHandler allows to get as many InputStreams and OutputStreams as you want.
     */
    public void enableMultiStreamHandler() {
        if(this.multiStreamHandler == null) {
            this.multiStreamHandler = new CMMultiStreamHandler(this);
        }
    }

    /**
     * Get the MultiStreamHandler. When the MultiStreamHandler is disabled, this will return null.
     * @return MutliStreamHandler or null if it is disabled
     */
    public CMMultiStreamHandler getMultiStreamHandler() {
        return this.multiStreamHandler;
    }

    // GET SOCKET INFORMATION
    /**
     * Get the IP Address
     * @return InetAddress
     */
    public InetAddress getIP() {
        return this.socket.getInetAddress();
    }

    /**
     * Get the port
     * @return Port
     */
    public int getPort() {
        return this.socket.getPort();
    }

    // GET SOCKET
    /**
     * Get socket
     * @return Socket
     * @deprecated Only use if you need it! Errors may occur! For example: If you use the socket's InputStream there are bytes missing. Use the copied InputStream (this.getInputStream) instead.
     */
    @Deprecated
    public Socket getSocket() {
        return this.socket;
    }

    // THREADS
    public boolean getThreadState() {
        return this.managerThread.isAlive();
    }

    // BYTE RECEIVING AND SENDING
    public void sendByte(int data) {
        if(!this.isClosed()) {
            try {
                this.socket.getOutputStream().write(data);
            } catch(IOException e) {
                this.close(ClientClosedReason.NO_REASON);
                this.fireEvent(new CMClientErrorEvent(this, e));
            }
        }
    }

    private void onByteReceived(int data) {
        this.inputStream.send(data);
        if(this.multiStreamHandler != null) {
            this.multiStreamHandler.send(data);
        }
        this.fireEvent(new CMClientByteReceivedEvent(this, data));
    }

    // EVENTS
    public void fireEvent(CMClientEvent event) {
        synchronized(this.eventQueue) {
            eventQueue.add(event);
        }
    }

    @Override
    public CMClient getEventClient() {
        return this;
    }
}
