package net.jandie1505.connectionmanager.server;

import java.io.IOException;
import java.io.OutputStream;

public class CMSOutputStream extends OutputStream {
    CMSClient client;

    public CMSOutputStream(CMSClient client) {
        this.client = client;
    }

    @Override
    public void write(int b) throws IOException {
        this.client.sendByte(b);
    }
}
