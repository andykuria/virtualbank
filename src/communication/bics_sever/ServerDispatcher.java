/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package communication.bics_sever;

import globalutils.LineModeEnum;
import java.util.*;

public class ServerDispatcher {

    private Vector mClients = new Vector();

    /**
     * Adds given client to the server's client list.
     */
    public synchronized void addClient(ClientInfo aClientInfo) {
        //mClients.removeAllElements();
        closeAllClients();
        mClients.add(aClientInfo);
    }

    /**
     * Deletes given client from the server's client list
     * if the client is in the list.
     */
    public synchronized void deleteClient(ClientInfo aClientInfo) {
        int clientIndex = mClients.indexOf(aClientInfo);
        if (clientIndex != -1) {
            ((ClientInfo)mClients.get(clientIndex)).close();
            mClients.removeElementAt(clientIndex);
        }
    }

    public synchronized void closeAllClients() {
        while (!mClients.isEmpty()) {
            ClientInfo topClient = (ClientInfo) mClients.get(0);
            topClient.mClientListener.close();
            mClients.remove(0);
        }
    }

    /**
     * Sends given message to all clients in the client list. Actually the
     * message is added to the client sender thread's message queue and this
     * client sender thread is notified.
     */
    public synchronized void sendMessageToAllClients(byte[] aMessage) {
        for (int i = 0; i < mClients.size(); i++) {
            ClientInfo clientInfo = (ClientInfo) mClients.get(i);
            clientInfo.mClientSender.sendMessageToClient(aMessage);
        }
    }

    public synchronized void sendMessageToAllClients(byte[] aMessage, LineModeEnum pLineMode, String pIndentifyCode) {
        switch (pLineMode) {
            case EBCDIC:
                for (int i = 0; i < mClients.size(); i++) {
                    ClientInfo clientInfo = (ClientInfo) mClients.get(i);
                    clientInfo.mClientSender.sendMessageToClient(aMessage, pLineMode, pIndentifyCode);
                }
                break;
            case ASCII:
            default:
                for (int i = 0; i < mClients.size(); i++) {
                    ClientInfo clientInfo = (ClientInfo) mClients.get(i);
                    clientInfo.mClientSender.sendMessageToClient(aMessage, pLineMode, pIndentifyCode);
                }
        }
    }

    public synchronized boolean isHavingConnection() {
        boolean aliveConnection = false;
        for (int i = 0; i < mClients.size(); i++) {
            ClientInfo clientInfo = (ClientInfo) mClients.get(i);
            if (clientInfo.mSocket.isConnected()) {
                aliveConnection = true;
            }
        }
        return aliveConnection;
    }

    public synchronized int size() {
        try {
            return mClients.size();
        } catch (Exception ex) {
            return 0;
        }
    }
}