package net.jandie1505.connectionmanager.enums;

public enum ConnectionBehavior {
    REFUSE(0),
    ACCEPT(1);

    private final int id;

    ConnectionBehavior(int id) {
        this.id = id;
    }

    public int getId() {
        return this.id;
    }
}
