package net.jandie1505.connectionmanager.enums;

public enum CloseEventReason {
    NO_REASON(0),
    DISCONNECTED_BY_USER(1),
    NO_RESPONSE(2);

    private final int id;

    private CloseEventReason(int id) {
        this.id = id;
    }

    public int getId() {
        return this.id;
    }
}
