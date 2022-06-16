package net.jandie1505.connectionmanager.utilities.dataiostreamhandler;

public enum DataIOStreamType {
    DEFAULT_TIMED(0),
    MULTI_STREAM_HANDLER_TIMED(1),
    MULTI_STREAM_HANDLER_CONSUMING(2);

    private final int id;

    DataIOStreamType(int id) {
        this.id = id;
    }

    public int getId() {
        return this.id;
    }
}
