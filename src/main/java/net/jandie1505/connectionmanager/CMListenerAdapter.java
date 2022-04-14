package net.jandie1505.connectionmanager;

import net.jandie1505.connectionmanager.events.CMClientByteReceivedEvent;
import net.jandie1505.connectionmanager.events.CMClientClosedEvent;
import net.jandie1505.connectionmanager.events.CMClientCreatedEvent;
import net.jandie1505.connectionmanager.events.CMClientEvent;
import net.jandie1505.connectionmanager.server.CMSServerEventListener;
import net.jandie1505.connectionmanager.server.events.CMSServerConnectionAcceptedEvent;
import net.jandie1505.connectionmanager.server.events.CMSServerEvent;
import net.jandie1505.connectionmanager.server.events.CMSServerStartListeningEvent;
import net.jandie1505.connectionmanager.server.events.CMSServerStopListeningEvent;
import net.jandie1505.connectionmanager.utilities.dataiostreamhandler.DataIOEventListener;
import net.jandie1505.connectionmanager.utilities.dataiostreamhandler.events.*;

public abstract class CMListenerAdapter implements CMClientEventListener, CMSServerEventListener, DataIOEventListener {

    // CLIENT

    public void onClientCreated(CMClientCreatedEvent event) {}

    public void onClientClosed(CMClientClosedEvent event) {}

    public void onClientByteReceived(CMClientByteReceivedEvent event) {}

    public void onUnknownClientEvent(CMClientEvent event) {}

    // SERVER

    public void onServerStartListening(CMSServerStartListeningEvent event) {}

    public void onServerStopListening(CMSServerStopListeningEvent event) {}

    public void onServerConnectionAccept(CMSServerConnectionAcceptedEvent event) {}

    public void onUnknownServerEvent(CMSServerEvent event) {}

    // DATA IO

    public void onByteReceived(DataIOByteReceivedEvent event) {}

    public void onBooleanReceived(DataIOBooleanReceivedEvent event) {}

    public void onShortReceived(DataIOShortReceivedEvent event) {}

    public void onCharReceived(DataIOCharReceivedEvent event) {}

    public void onIntReceived(DataIOIntReceivedEvent event) {}

    public void onLongReceivedEvent(DataIOLongReceivedEvent event) {}

    public void onFloatReceived(DataIOFloatReceivedEvent event) {}

    public void onDoubleReceived(DataIODoubleReceivedEvent event) {}

    public void onUTFReceived(DataIOUTFReceivedEvent event) {}

    // EVENT LISTENERS

    @Override
    public void onEvent(CMClientEvent event) {
        if(event instanceof CMClientCreatedEvent) {
            onClientCreated((CMClientCreatedEvent) event);
        } else if(event instanceof CMClientClosedEvent) {
            onClientClosed((CMClientClosedEvent) event);
        } else if(event instanceof CMClientByteReceivedEvent) {
            onClientByteReceived((CMClientByteReceivedEvent) event);
        } else {
            onUnknownClientEvent(event);
        }
    }

    @Override
    public void onEvent(CMSServerEvent event) {
        if(event instanceof CMSServerStartListeningEvent) {
            onServerStartListening((CMSServerStartListeningEvent) event);
        } else if(event instanceof CMSServerStopListeningEvent) {
            onServerStopListening((CMSServerStopListeningEvent) event);
        } else if(event instanceof CMSServerConnectionAcceptedEvent) {
            onServerConnectionAccept((CMSServerConnectionAcceptedEvent) event);
        } else {
            onUnknownServerEvent(event);
        }
    }

    @Override
    public void onEvent(DataIOEvent event) {
        if(event instanceof DataIOByteReceivedEvent) {
            onByteReceived((DataIOByteReceivedEvent) event);
        } else if(event instanceof DataIOBooleanReceivedEvent) {
            onBooleanReceived((DataIOBooleanReceivedEvent) event);
        } else if(event instanceof DataIOShortReceivedEvent) {
            onShortReceived((DataIOShortReceivedEvent) event);
        } else if(event instanceof DataIOCharReceivedEvent) {
            onCharReceived((DataIOCharReceivedEvent) event);
        } else if(event instanceof DataIOIntReceivedEvent) {
            onIntReceived((DataIOIntReceivedEvent) event);
        } else if(event instanceof DataIOLongReceivedEvent) {
            onLongReceivedEvent((DataIOLongReceivedEvent) event);
        } else if(event instanceof DataIOFloatReceivedEvent) {
            onFloatReceived((DataIOFloatReceivedEvent) event);
        } else if(event instanceof DataIODoubleReceivedEvent) {
            onDoubleReceived((DataIODoubleReceivedEvent) event);
        } else if(event instanceof DataIOUTFReceivedEvent) {
            onUTFReceived((DataIOUTFReceivedEvent) event);
        }
    }
}
