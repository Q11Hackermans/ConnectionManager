package net.jandie1505.connectionmanager.server;

import net.jandie1505.connectionmanager.enums.CloseEventReason;
import net.jandie1505.connectionmanager.server.actions.CMSClientAction;
import net.jandie1505.connectionmanager.server.events.CMSClientCloseEvent;
import net.jandie1505.connectionmanager.server.events.CMSClientCreatedEvent;
import net.jandie1505.connectionmanager.server.events.CMSClientEvent;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CMSClient {
    UUID id;
    private Socket socket;
    private List<CMSClientEventListener> listeners;
    private List<CMSClientAction> actions;
    private Thread managerThread;
    private Thread actionThread;

    public CMSClient(Socket socket) {
        this.id = UUID.randomUUID();
        listeners = new ArrayList<>();
        actions = new ArrayList<>();
        this.socket = socket;

        this.fireEvent(new CMSClientCreatedEvent(this));

        this.managerThread = new Thread(() -> {
            while(!Thread.currentThread().isInterrupted() && !socket.isClosed()) {
                try {
                    if(socket.getInputStream().read() == -1) {
                        socket.close();
                        fireEvent(new CMSClientCloseEvent(this, CloseEventReason.NO_RESPONSE));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        managerThread.start();

        this.actionThread = new Thread(() -> {
            while(!Thread.currentThread().isInterrupted() && !socket.isClosed()) {
                try {
                    for(CMSClientAction action : actions) {
                        action.run(this);
                    }
                } catch(Exception e) {
                    e.printStackTrace();
                }
            }
        });
        actionThread.start();
    }

    /**
     * Get the unique ID of the client
     * @return Unique ID (UUID)
     */
    public UUID getUniqueId() {
        return this.id;
    }

    /**
     * Add an EventListener
     * @param listener CMSClientEventListener
     */
    public void addEventListener(CMSClientEventListener listener) {
        this.listeners.add(listener);
    }

    /**
     * Remove an EventListener
     * @param listener CMSClientEventListener
     */
    public void removeEventListener(CMSClientEventListener listener) {
        this.listeners.remove(listener);
    }

    /**
     * Get the event listeners
     * @return Listener list
     */
    public List<CMSClientEventListener> getListeners() {
        return this.listeners;
    }

    /**
     * Add an action
     * @param action CMSAction
     */
    public void addAction(CMSClientAction action) {
        this.actions.add(action);
    }

    /**
     * Remove an action
     * @param action CMSAction
     */
    public void removeAction(CMSClientAction action) {
        this.actions.remove(action);
    }

    /**
     * Get the action list
     * @return Action list
     */
    public List<CMSClientAction> getActions() {
        return this.actions;
    }

    /**
     * Close the connections and Input/Output streams
     * @throws IOException IOException
     */
    public void close() throws IOException {
        this.socket.close();
        this.fireEvent(new CMSClientCloseEvent(this, CloseEventReason.DISCONNECTED_BY_USER));
    }

    /**
     * Get the InputStream
     * @return InputStream
     * @throws IOException IOException
     */
    public InputStream getInputStream() throws IOException {
        return this.socket.getInputStream();
    }

    /**
     * Get the OutputStream
     * @return OutputStream
     * @throws IOException IOException
     */
    public OutputStream getOutputStream() throws IOException {
        return this.socket.getOutputStream();
    }

    /**
     * Get the closed and connection state
     * @return
     */
    public boolean isClosed() {
        return this.socket.isClosed();
    }

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

    /**
     * Get socket
     * @return Socket
     * @deprecated use other methods if possible
     */
    @Deprecated
    public Socket getSocket() {
        return this.socket;
    }

    /**
     * Returns true if the manager thread is running
     * @return Manager thread alive
     */
    public boolean managerThreadAlive() {
        return managerThread.isAlive();
    }

    /**
     * Returns true if the action thread is alive
     * @return Action thread alive
     */
    public boolean actionThreadAlive() {
        return actionThread.isAlive();
    }

    /**
     * Fire an event to all event listeners
     * @param event Event
     */
    public void fireEvent(CMSClientEvent event) {
        for(CMSClientEventListener listener : this.listeners) {
            listener.onEvent(event);
        }
    }
}
