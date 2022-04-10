package net.jandie1505.connectionmanager.enums;

public enum CloseEventReason {
    NO_REASON(0),
    CONNECTION_CLOSED(1),
    DISCONNECTED_BY_REMOTE(2),
    CONNECTION_RESET(3),
    CONNECTION_FAILED(4),
    ERROR(5);

    private final int id;

    private CloseEventReason(int id) {
        this.id = id;
    }

    public int getId() {
        return this.id;
    }
}
