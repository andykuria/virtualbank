/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package communication.bics_sever;

import cfg.cfgNode;
import ginterface.imsgincomming;
import ginterface.iqueryactionable;

import iso8583.ConfigIsoMessage;
import iso8583.HeaderProcessing;
import iss.showLogEnum;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.ServerSocket;
import java.io.DataInputStream;
import lib.CommonLib;
import processing.systemMessageQueue;
import unisim201401.systemLoader;


/**
 *
 * @author minhdbh
 * 
 */
public class bnvServer extends Thread {

    private int port;
    private DataInputStream inCommingData;
    private PrintWriter ClientWriter = null;
    private Socket bnvClient = null;
    private ServerSocket bnvServerSock = null;
    private boolean isRunning = false;
    ServerDispatcher serverDispatcher;
    private String instituttionCode = "";
    private int instituttionIndex = -1;
    private String identifyState = "";
    HeaderProcessing msgheaderInfo;
    systemMessageQueue incommingqueue;
    private cfgNode isoCfg;
    
    private iqueryactionable instData;
    private Thread parentThread = null;

     private systemLoader systemGlobal;

    public void setSystemGlobal(systemLoader systemGlobal) {
        this.systemGlobal = systemGlobal;
    }
    public bnvServer() {
        isRunning = true;
    }

    @Override
    public void run() {
        openSocket();
        while (isRunning) {
            try {
                bnvClient = bnvServerSock.accept();
                CommonLib.PrintScreen(systemGlobal, instituttionCode + ": " + bnvClient.getRemoteSocketAddress() + " is connected...on " + String.valueOf(bnvClient.getLocalSocketAddress()), showLogEnum.DEFAULT);
                //--CommonLib.PrintScreen("ShowVNBCSocketState", "VNBC Client "+bnvClient.getLocalAddress()+ " is connected...on "+String.valueOf(bnvClient.getPort()));
                //--GlobalObject.log_system_action.addData("VNBC Client "+bnvClient.getLocalAddress()+ " is connected...on "+String.valueOf(bnvClient.getPort()));
                //* Warning Added */
                //--GlobalObject.warning_log_file.addData("VNBC\tVNBC\t00\tNew connection is accepted from VNBC");
                serverDispatcher.closeAllClients();
                if (serverDispatcher.isHavingConnection()) {
                    bnvClient.close();
                    //--CommonLib.PrintScreen("ShowVNBCSocketState", "VNBC try to make more 1 connection to banknetvn... connection was been closed");
                    //--GlobalObject.log_system_action.addData("VNBC try to make more 1 connection to banknetvn... connection was been closed");
                } else {
                    //--GlobalObject.SystemReady.remove("SOCK_VNBC");
                    ClientInfo clientInfo = new ClientInfo();
                    clientInfo.mSocket = bnvClient;
                    bnvListener clientListener = new bnvListener(clientInfo, serverDispatcher);
                    clientListener.setDataLineMode(instData.getLineMode());
                    clientListener.setIncommingqueue(incommingqueue);
                    clientListener.setIdentifyState(identifyState);
                    clientListener.setIsoCfg(isoCfg);
                    
                    clientListener.setInstituttionIndex(instituttionIndex);
                    clientListener.setInstituttionCode(instituttionCode);
                    clientListener.setInstData(instData);
                    clientListener.setParentThread(parentThread);
                    clientListener.setMsgheaderInfo(msgheaderInfo);
                    clientListener.setSystemGlobal(systemGlobal);

                    clientListener.setLineMode(instData.getLineMode());
                    bnvSender clientSender = new bnvSender(clientInfo);
                    clientSender.setSystemGlobal(systemGlobal);
                    clientInfo.mClientListener = clientListener;
                    clientInfo.mClientSender = clientSender;
                    
                    serverDispatcher.addClient(clientInfo);
                    clientListener.start();
                }
            } catch (Exception ex) {
                //CommonLib.PrintScreen(ex.getMessage());
                try {
                    bnvClient.close();
                } catch (Exception ex1) {
                }
                //--CommonLib.addSystemState("SOCK_VNBC", "VNBC-SOCKET");
                //--CommonLib.PrintScreen("ShowVNBCSocketState","VNBC Client connection error occurs "+ex.getMessage());
                //--GlobalObject.log_system_action.addData("ERROR - VNBC Server error, couldn't make a connection for Client"+ex.getMessage());
            }
        }
    }

    private void openSocket() {
        try {
            bnvServerSock = new ServerSocket(port);
            CommonLib.PrintScreen(systemGlobal, instituttionCode + " is listening on port " + port, showLogEnum.DEFAULT);
            serverDispatcher = new ServerDispatcher();
            isRunning = true;
            //CommonLib.PrintScreen("Listening started on port " + String.valueOf(port));
            //--CommonLib.PrintScreen("ShowVNBCSocketState","Listening started on port " + String.valueOf(port));
            //--GlobalObject.log_system_action.addData("Listening started on port " + String.valueOf(port));
        } catch (Exception ex) {
            isRunning = false;
            //--CommonLib.addSystemState("SOCK_VNBC", "VNBC-SOCKET");
            //CommonLib.PrintScreen("Error occur: Could not listen on port " + String.valueOf(port));
            //--CommonLib.PrintScreen("ShowVNBCSocketState","Error occur: Could not listen on port " + String.valueOf(port));
            //--GlobalObject.log_system_action.addData("ERROR - Could not listen on port " + String.valueOf(port));
        }
    }

    /**
     * Set port of server which is listening at the org's server
     * @param value: port listener at server
     */
    public void setPort(int value) {
        port = value;
    }

    public int getPort() {
        return port;
    }

    public void stopServer() {
        isRunning = false;
        try {
            //Client.close();
            serverDispatcher.closeAllClients();
            //--CommonLib.PrintScreen("ShowVNBCSocketState","All Server socket stopped..");
        } catch (Exception ex) {
            //System.out.print("Couldn't stop server socket: " +ex.getMessage());
            //--CommonLib.PrintScreen("ShowVNBCSocketState","Couldn't stop server socket: " +ex.getMessage());
        }

        try {
            bnvServerSock.close();
            //System.out.print("Server socket stopped..");
            //--CommonLib.PrintScreen("ShowVNBCSocketState","Server socket stopped..");
        } catch (Exception ex) {
            //System.out.print("Couldn't stop server socket: " +ex.getMessage());
            //--CommonLib.PrintScreen("ShowVNBCSocketState","Couldn't stop server socket: " +ex.getMessage());
        }
        //--CommonLib.addSystemState("SOCK_VNBC", "VNBC-SOCKET");
    }

    public void sendAllClients(String value) {
        serverDispatcher.sendMessageToAllClients(value.getBytes(), instData.getLineMode(), identifyState);
        //CommonLib.PrintScreen(identifyState+" send: "+CommonLib.getHumanFormatFromByte(value.getBytes()));
    }

    public void sendAllClients(byte[] value) {
        serverDispatcher.sendMessageToAllClients(value, instData.getLineMode(), identifyState);
        //CommonLib.PrintScreen(identifyState+" send: "+CommonLib.getHumanFormatFromByte(value));
    }



    public void setMsgheaderInfo(HeaderProcessing pmsgheaderInfo) {
        msgheaderInfo = pmsgheaderInfo;
    }

    public void setInstituttionIndex(int pinstituttionIndex) {
        instituttionIndex = pinstituttionIndex;
    }

    public int getInstituttionIndex() {
        return instituttionIndex;
    }

    public String getInstituttionCode() {
        return instituttionCode;
    }

    public void setInstituttionCode(String pinstituttionCode) {
        this.instituttionCode = pinstituttionCode;
    }

    public void setIdentifyState(String pidentifyState) {
        this.identifyState = pidentifyState;
    }

    public void setIncommingqueue(systemMessageQueue pincommingqueue) {
        this.incommingqueue = pincommingqueue;
    }



    public void setInstData(iqueryactionable pinstData) {
        this.instData = pinstData;
    }

    public void setParentThread(Thread pThread) {
        this.parentThread = pThread;
    }

    public boolean hasClients() {
        if (serverDispatcher != null) {
            return serverDispatcher.isHavingConnection();
        }
        else
        {
            return false;
        }
    }

    public int totalClients() {
        return serverDispatcher.size();
    }

    public void setIsoCfg(cfgNode oCfg) {
        this.isoCfg=oCfg;
    }
}
