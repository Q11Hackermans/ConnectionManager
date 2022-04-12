package net.jandie1505.connectionmanager.utilities;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.CountDownLatch;

public class CMInputStream extends InputStream {
    CountDownLatch latch;
    int b;

    public CMInputStream() {
        latch = new CountDownLatch(1);
        b = -2;
    }

    @Override
    public int read() throws IOException {
        try {
            latch.await();
            return b;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public void send(int b) {
        if(b >= 0) {
            this.b = b;
            latch.countDown();
        }
    }
}
