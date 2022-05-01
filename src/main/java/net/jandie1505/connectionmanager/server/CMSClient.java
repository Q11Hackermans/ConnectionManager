package net.jandie1505.connectionmanager.server;

import net.jandie1505.connectionmanager.CMClient;
import net.jandie1505.connectionmanager.CMClientEventListener;

import java.net.Socket;
import java.util.List;
import java.util.UUID;

/**
 * Server-side client (CMS = ConnectionManager Server)
 */
public class CMSClient extends CMClient {
    private CMSServer server;

    public CMSClient(Socket socket, CMSServer server) {
        super(socket, server);
    }

    public CMSClient(Socket socket, CMSServer server, List<CMClientEventListener> listeners) {
        super(socket, listeners, server);
    }

    @Override
    public void setup(Object[] constructorParameters) {
        this.server = (CMSServer) constructorParameters[0];
    }

    @Override
    public boolean stopcondition() {
        return (this.server == null || this.server.isClosed());
    }

    /**
     * Get the unique ID of the client
     * @return Unique ID (UUID)
     */
    public UUID getUniqueId() {
        return this.server.getIdOfClient(this);
    }

    /**
     * Returns the server
     * @return CMSServer
     */
    public CMSServer getServer() {
        return this.server;
    }
}
