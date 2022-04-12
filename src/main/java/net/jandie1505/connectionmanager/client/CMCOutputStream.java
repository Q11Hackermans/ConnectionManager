package net.jandie1505.connectionmanager.client;

import java.io.IOException;
import java.io.OutputStream;

public class CMCOutputStream extends OutputStream {
    CMCClient client;

    public CMCOutputStream(CMCClient client) {
        this.client = client;
    }

    @Override
    public void write(int b) throws IOException {
        this.client.sendByte(b);
    }
}
