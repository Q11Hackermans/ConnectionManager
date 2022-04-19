package net.jandie1505.connectionmanager.server;

import net.jandie1505.connectionmanager.enums.PendingClientState;

import java.net.Socket;

public class CMSPendingClient {
    private final Socket socket;
    private PendingClientState state;
    private long time;

    public CMSPendingClient(Socket socket, long time) {
        this.socket = socket;
        this.state = PendingClientState.DEFAULT;
        this.time = time;
    }

    public Socket getSocket() {
        return this.socket;
    }

    public PendingClientState getState() {
        return this.state;
    }

    public void setState(PendingClientState state) {
        this.state = state;
    }

    public long getTime() {
        return this.time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
