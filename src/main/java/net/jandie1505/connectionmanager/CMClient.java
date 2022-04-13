package net.jandie1505.connectionmanager;

import net.jandie1505.connectionmanager.enums.ClientClosedReason;
import net.jandie1505.connectionmanager.events.CMClientByteReceivedEvent;
import net.jandie1505.connectionmanager.events.CMClientClosedEvent;
import net.jandie1505.connectionmanager.events.CMClientCreatedEvent;
import net.jandie1505.connectionmanager.events.CMClientEvent;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract class CMClient {
    private Socket socket;
    private List<CMClientEventListener> listeners;
    private List<CMClientEvent> eventQueue;
    private Thread managerThread;
    private Thread eventQueueThread;
    private CMInputStream inputStream;
    private CMOutputStream outputStream;

    // SETUP
    public CMClient(Socket socket) {
        this.listeners = new ArrayList<>();
        this.setup1(socket, null);
    }

    public CMClient(Socket socket, Collection<CMClientEventListener> listeners) {
        this.listeners = new ArrayList<>();
        this.listeners.addAll(listeners);
        this.setup1(socket, null);
    }

    public CMClient(Socket socket, Object... constructorParameters) {
        this.listeners = new ArrayList<>();
        this.setup1(socket, constructorParameters);
    }

    public CMClient(Socket socket, Collection<CMClientEventListener> listeners, Object... constructorParameters) {
        this.listeners = new ArrayList<>();
        this.listeners.addAll(listeners);
        this.setup1(socket, constructorParameters);
    }

    private void setup1(Socket socket, Object[] constructorParameters) {
        this.socket = socket;

        this.inputStream = new CMInputStream(this);
        this.outputStream = new CMOutputStream(this);

        this.eventQueue = new ArrayList<>();

        this.managerThread = new Thread(() -> {
            while(!Thread.currentThread().isInterrupted() && !socket.isClosed()) {
                try {
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
            }
        });
        managerThread.start();

        this.eventQueueThread = new Thread(() -> {
            while(!Thread.currentThread().isInterrupted() && !socket.isClosed()) {
                if(eventQueue != null && eventQueue.size() > 0) {
                    for(CMClientEventListener listener : this.listeners) {
                        try {
                            listener.onEvent(eventQueue.remove(0));
                        } catch(Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
        eventQueueThread.setName(this + "-EventHandlerThread");
        eventQueueThread.start();

        this.setup(constructorParameters);

        new Thread(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            this.fireEvent(new CMClientCreatedEvent(this));
        }).start();
    }

    /**
     * For setup of subclasses
     */
    protected void setup(Object[] constructorParameters) {}

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
        if(!this.isClosed()) {
            this.fireEvent(new CMClientClosedEvent(this, ClientClosedReason.CONNECTION_CLOSED));
        }
        this.close1();
    }

    /**
     * For subclasses
     */
    protected void onClose() {}

    /**
     * Close the connection, the Input/Output streams and shutdown the threads (with a specific reason)
     *
     */
    public void close(ClientClosedReason reason) {
        if(!this.isClosed()) {
            this.fireEvent(new CMClientClosedEvent(this, reason));
        }
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
    public InputStream getInputStream() {
        return this.inputStream;
    }

    /**
     * Get the OutputStream of the client.
     * THIS IS NOT THE OUTPUT STREAM OF THE SOCKET! IT IS A COPY OF IT!
     * USE THIS OUTPUT STREAM INSTEAD OF THE SOCKET OUTPUT STREAM TO AVOID ERRORS!
     * @return OutputStream
     */
    public OutputStream getOutputStream() {
        return this.outputStream;
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
    public void sendByte(int data) throws IOException {
        if(data >= 0 && data <= 255) {
            this.socket.getOutputStream().write(data);
        } else {
            throw new IllegalArgumentException("A byte can only be in range of 0-255");
        }
    }

    private void onByteReceived(int data) {
        this.inputStream.send(data);
        this.fireEvent(new CMClientByteReceivedEvent(this, data));
    }

    protected void fireEvent(CMClientEvent event) {
        eventQueue.add(event);
    }
}
