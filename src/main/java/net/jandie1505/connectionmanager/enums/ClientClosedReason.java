package net.jandie1505.connectionmanager.enums;

public enum ClientClosedReason {
    NO_REASON(0),

    /**
     * Client was closed via CMClient.close()
     */
    CONNECTION_CLOSED(1),

    /**
     * Connection was closed by the remote client
     */
    DISCONNECTED_BY_REMOTE(2),

    /**
     * The connection to the remote client is disconnected without the remote client closing the connection.
     */
    CONNECTION_RESET(3),

    /**
     * The connection failed (When socket.isConnected() == false).
     */
    CONNECTION_FAILED(4);

    private final int id;

    private ClientClosedReason(int id) {
        this.id = id;
    }

    public int getId() {
        return this.id;
    }
}
