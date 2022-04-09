package net.jandie1505.connectionmanager.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;

public class CMServer implements {
    private ServerSocket server;
    private List<CMServerClient> clients;
    private List<CMEventListener> globalListeners;
    Thread thread;

    public CMServer(int port) throws IOException {
        this.clients = new ArrayList<>();
        this.server = new ServerSocket(port);
    }

    /**
     * Starts listening for connection attempts and accepting them
     */
    public void startListen() {
        if(this.thread != null && this.thread.isAlive()) {
            this.thread.stop();
        }

        this.thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while(!Thread.currentThread().isInterrupted()) {
                    try {
                        clients.add(new CMServerClient(server.accept()));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    /**
     * Stops listening for connection attempts and accepting them
     */
    public void stopListen() {
        this.thread.stop();
        this.thread = null;
    }

    public void test() {
    }

    public void closeAll() throws IOException {
        for(CMServerClient client : this.clients) {
            client.close();
        }
    }
}
