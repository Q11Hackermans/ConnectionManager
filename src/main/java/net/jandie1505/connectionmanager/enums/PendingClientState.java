package net.jandie1505.connectionmanager.enums;

public enum PendingClientState {
    DEFAULT(0),
    DENIED(1),
    ACCEPTED(2);

    private final int id;

    PendingClientState(int id) {
        this.id = id;
    }

    public int getId() {
        return this.id;
    }
}
