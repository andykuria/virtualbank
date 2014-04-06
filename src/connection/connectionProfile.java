/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package connection;

import cfg.cfgNode;
import communication.bics_client.bnvClient;
import communication.bics_sever.bnvServer;
import ginterface.iconnection;
import ginterface.iqueryactionable;
import globalutils.ConnectionMode;
import iss.showLogEnum;

import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import lib.CommonLib;
import processing.systemMessageQueue;
import unisim201401.systemLoader;

/**
 *
 * @author kt1
 */
public class connectionProfile extends Thread implements iconnection {

    private String instituttionCode = "";
    private int instituttionIndex = -1;
    systemMessageQueue incommingqueue;
    private cfgNode isoCfg;
    private iqueryactionable instData;
    ConnectionMode cnnMode;
    private boolean threadStatus = false;
    private bnvClient[] clients;
    private bnvServer[] servers;
    private Thread parentThread = null;
    //Current support State: Communication LogicState: BIN DOWN, SIGNOFF
    //Status: (sign-off message)
    //ZPK_IN ZPK_OUT TAK_IN TAK_OUT: Key Echange error
    private int indexPort = 0; //using only when singke mode 4 - for load balancing
    private ConcurrentHashMap systemState;
    private systemLoader systemGlobal;

    public void setSystemGlobal(systemLoader systemGlobal) {
        this.systemGlobal = systemGlobal;
    }

    public void setParentThread(Thread pThread) {
        this.parentThread = pThread;
    }

    public connectionProfile() {
        cnnMode = ConnectionMode.DUPLEX_CLIENT;
        systemState = new ConcurrentHashMap();

    }
    private int seq_timer = 0;
    private boolean connectionStop = false;

    @Override
    public void run() {
        initConnection();
        while (true) {

            try {
                cnnMode = instData.getConnMode();
                switch (cnnMode) {
                    case DUPLEX_CLIENT:
                    case SINGLE4:

                        seq_timer++;
                        if (seq_timer > 2) {
                            refresh();
                            seq_timer = 0;
                        }
                        // if clients[0].getStatus()

                        break;
                    case DUPLEX_SERVER:

                        break;

                }

                Thread.sleep(1800);
            } catch (Exception ex) {
            }
        }
    }

    private void initConnection() {
        cnnMode = ConnectionMode.valueOf(instData.getInstConfig().getValue("MODE"));
        switch (cnnMode) {
            case DUPLEX_CLIENT:

                clients[0].start();
                break;
            case DUPLEX_SERVER:
                servers[0].start();
                break;
            case MULTI_SERVER:
                if (servers != null) {
                    if (servers.length > 0) {
                        for (int i = 0; i < servers.length; i++) {
                            servers[i].start();
                        }
                    }
                }
                break;
            case SINGLE4:
                servers[0].start();
                servers[1].start();
                int i = 0;
                while (i < 10) {
                    try {
                        i++;
                        if (servers[0].hasClients()) {
                            break;
                        }
                        Thread.sleep(100);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(connectionProfile.class.getName()).log(Level.SEVERE, null, ex);
                    }

                }
                clients[0].start();
                clients[1].start();

                break;
        }

    }

    public void init() {
        //0: Duplex mode (listening service)
        //1: Duplex mode (Connector service)
        //2: duplex mode 2 port (1 lst + 1 conn)
        //3: Single mode 2 port (1 lst + 1 conn)

        //4: Single mode 4 port(2 lst + 2 conn)
        cnnMode = ConnectionMode.valueOf(instData.getInstConfig().getValue("MODE"));
        switch (cnnMode) {
            case DUPLEX_CLIENT:

                initDuplexClient();
                break;
            case DUPLEX_SERVER:
                initDuplexServer();
                break;
            case MULTI_SERVER:
                initMultiServers();
                break;
            case SINGLE4:
                initSingle4Servers();

                initSingle4Clients();

                break;
            case SINGLE2:
                clients = new bnvClient[1];
                clients[0] = new bnvClient(instData.getInstConfig().getValue("INTERFACECODE"));
                clients[0].setPort(instData.getInstConfig().getIntValue("PORT"));
                clients[0].setServer(instData.getInstConfig().getValue("IP"));
                clients[0].setLocalPort(instData.getInstConfig().getIntValue("LOCALPORT"));
                clients[0].setIncommingqueue(incommingqueue);
                clients[0].setIdentifyState(instData.getInstConfig().getValue("INTERFACECODE") + "_SC_SOCK_" + instData.getInstConfig().getValue("PORT").toString());
                clients[0].setIsoCfg(isoCfg);

                clients[0].setInstituttionIndex(instituttionIndex);
                clients[0].setInstData(instData);
                clients[0].setSystemGlobal(systemGlobal);

                clients[0].setParentThread(parentThread);
                if (!instData.getInstConfig().getValue("DATAMODE").toUpperCase().equals("ASCII")) {
                    clients[0].setMsgheaderInfo(instData.getHeaderInfo());
                }

                servers = new bnvServer[1];

            default:
                break;
        }
    }

    private void initDuplexClient() {
        clients = new bnvClient[1];
        clients[0] = new bnvClient(instData.getInstConfig().getValue("INTERFACECODE"));
        clients[0].setPort(instData.getInstConfig().getIntValue("PORT"));
        clients[0].setServer(instData.getInstConfig().getValue("IP"));
        clients[0].setLocalPort(instData.getInstConfig().getIntValue("LOCALPORT"));
        clients[0].setIncommingqueue(incommingqueue);
        clients[0].setIdentifyState(instData.getInstConfig().getValue("INTERFACECODE") + "_DC_SOCK_" + instData.getInstConfig().getValue("PORT").toString());
        clients[0].setIsoCfg(isoCfg);

        clients[0].setInstituttionIndex(instituttionIndex);
        clients[0].setInstData(instData);

        clients[0].setParentThread(parentThread);
        clients[0].setSystemGlobal(systemGlobal);
        if (!instData.getInstConfig().getValue("DATAMODE").toUpperCase().equals("ASCII")) {
            clients[0].setMsgheaderInfo(instData.getHeaderInfo());
        }

        clients[0].setParentThread(parentThread);
        //clients[0].start();
    }

    private void initDuplexServer() {
        servers = new bnvServer[1];
        servers[0] = new bnvServer();
        servers[0].setPort(instData.getInstConfig().getIntValue("PORT"));
        servers[0].setIncommingqueue(incommingqueue);
        servers[0].setIdentifyState(instData.getInstConfig().getValue("INTERFACECODE") + "_DS_SOCK_" + instData.getInstConfig().getValue("PORT").toString());
        servers[0].setIsoCfg(isoCfg);

        servers[0].setInstituttionIndex(instituttionIndex);
        servers[0].setInstituttionCode(instituttionCode);
        servers[0].setInstData(instData);
        servers[0].setParentThread(parentThread);

        servers[0].setMsgheaderInfo(instData.getHeaderInfo());
        servers[0].setSystemGlobal(systemGlobal);

        //servers[0].start();
    }

    private void initMultiServers() {
        String portsStr[] = instData.getInstConfig().getValue("PORTS").split(" ");

        servers = new bnvServer[portsStr.length];
        for (int i = 0; i < portsStr.length; i++) {
            servers[i] = new bnvServer();
            servers[i].setPort(CommonLib.valueOf(portsStr[i]));
            servers[i].setIncommingqueue(incommingqueue);
            servers[i].setIdentifyState(instData.getInstConfig().getValue("INTERFACECODE") + "_MS_SOCK_" + portsStr[i]);
            servers[i].setIsoCfg(isoCfg);

            servers[i].setInstituttionIndex(instituttionIndex);
            servers[i].setInstituttionCode(instituttionCode);
            servers[i].setInstData(instData);
            servers[i].setParentThread(parentThread);
            servers[i].setMsgheaderInfo(instData.getHeaderInfo());
            servers[i].setSystemGlobal(systemGlobal);

        }

        //servers[0].start();
    }

    private void initSingle4Servers() {
        servers = new bnvServer[2];
        servers[0] = new bnvServer();
        servers[0].setPort(instData.getInstConfig().getIntValue("SPORT1"));
        servers[0].setIncommingqueue(incommingqueue);
        servers[0].setIdentifyState(instData.getInstConfig().getValue("INTERFACECODE") + "_SS4_SOCKET_0_" + instData.getInstConfig().getValue("SPORT1").toString());
        servers[0].setIsoCfg(isoCfg);

        servers[0].setInstituttionIndex(instituttionIndex);
        servers[0].setInstituttionCode(instituttionCode);
        servers[0].setInstData(instData);
        servers[0].setParentThread(parentThread);
        servers[0].setSystemGlobal(systemGlobal);

        servers[1] = new bnvServer();
        servers[1].setPort(instData.getInstConfig().getIntValue("SPORT2"));
        servers[1].setIncommingqueue(incommingqueue);
        servers[1].setIdentifyState(instData.getInstConfig().getValue("INTERFACECODE") + "_SS4_SOCKET_1_" + instData.getInstConfig().getValue("SPORT2").toString());
        servers[1].setIsoCfg(isoCfg);

        servers[1].setInstituttionIndex(instituttionIndex);
        servers[1].setInstituttionCode(instituttionCode);
        servers[1].setInstData(instData);
        servers[1].setParentThread(parentThread);
        servers[1].setSystemGlobal(systemGlobal);

        if (!instData.getInstConfig().getValue("DATAMODE").toUpperCase().equals("ASCII")) {
            servers[0].setMsgheaderInfo(instData.getHeaderInfo());
            servers[1].setMsgheaderInfo(instData.getHeaderInfo());
        }
    }

    private void restartSingle4Client(int pIndexPort) {
        clients[pIndexPort] = new bnvClient(instData.getInstConfig().getValue("INTERFACECODE"));
        clients[pIndexPort].setPort(instData.getInstConfig().getIntValue("PORT" + String.valueOf(pIndexPort + 1)));
        clients[pIndexPort].setServer(instData.getInstConfig().getValue("IP" + String.valueOf(pIndexPort + 1)));
        clients[pIndexPort].setLocalPort(instData.getInstConfig().getIntValue("LOCALPORT" + String.valueOf(pIndexPort + 1)));
        clients[pIndexPort].setIncommingqueue(incommingqueue);
        clients[pIndexPort].setIdentifyState(instData.getInstConfig().getValue("INTERFACECODE") + "_SC4_SOCKET_" + pIndexPort + "_" + instData.getInstConfig().getValue("PORT" + String.valueOf(pIndexPort + 1)).toString());
        clients[pIndexPort].setIsoCfg(isoCfg);

        clients[pIndexPort].setInstituttionIndex(instituttionIndex);
        clients[pIndexPort].setInstData(instData);
        clients[pIndexPort].setSystemGlobal(systemGlobal);
        if (pIndexPort == 1) {
            clients[pIndexPort].setMasterInterface(false);
        }
        clients[pIndexPort].setParentThread(parentThread);
        if (!instData.getInstConfig().getValue("DATAMODE").toUpperCase().equals("ASCII")) {
            clients[pIndexPort].setMsgheaderInfo(instData.getHeaderInfo());
        }
    }

    private void initSingle4Clients() {
        clients = new bnvClient[2];
        clients[0] = new bnvClient(instData.getInstConfig().getValue("INTERFACECODE"));
        clients[0].setPort(instData.getInstConfig().getIntValue("PORT1"));
        clients[0].setServer(instData.getInstConfig().getValue("IP1"));
        clients[0].setLocalPort(instData.getInstConfig().getIntValue("LOCALPORT1"));
        clients[0].setIncommingqueue(incommingqueue);
        clients[0].setIdentifyState(instData.getInstConfig().getValue("INTERFACECODE") + "_SC4_SOCKET_0_" + instData.getInstConfig().getValue("PORT1").toString());
        clients[0].setIsoCfg(isoCfg);

        clients[0].setInstituttionIndex(instituttionIndex);
        clients[0].setInstData(instData);

        clients[0].setParentThread(parentThread);
        clients[0].setSystemGlobal(systemGlobal);
        if (!instData.getInstConfig().getValue("DATAMODE").toUpperCase().equals("ASCII")) {
            clients[0].setMsgheaderInfo(instData.getHeaderInfo());
        }

        clients[1] = new bnvClient(instData.getInstConfig().getValue("INTERFACECODE"));
        clients[1].setPort(instData.getInstConfig().getIntValue("PORT2"));
        clients[1].setServer(instData.getInstConfig().getValue("IP2"));
        clients[1].setLocalPort(instData.getInstConfig().getIntValue("LOCALPORT2"));
        clients[1].setIncommingqueue(incommingqueue);
        clients[1].setIdentifyState(instData.getInstConfig().getValue("INTERFACECODE") + "_SC4_SOCKET_1_" + instData.getInstConfig().getValue("PORT2").toString());
        clients[1].setIsoCfg(isoCfg);

        clients[1].setInstituttionIndex(instituttionIndex);
        clients[1].setInstData(instData);
        clients[1].setMasterInterface(false);

        clients[1].setParentThread(parentThread);
         clients[1].setSystemGlobal(systemGlobal);
        if (!instData.getInstConfig().getValue("DATAMODE").toUpperCase().equals("ASCII")) {
            clients[1].setMsgheaderInfo(instData.getHeaderInfo());
        }

    }

    public void sendData(byte[] value, int pIndexPort) {
        cnnMode = ConnectionMode.valueOf(instData.getInstConfig().getValue("MODE"));
        switch (cnnMode) {
            case DUPLEX_CLIENT:
                if (clients != null) {
                    if (clients.length > 0) {
                        clients[0].sendData(value);
                    }
                }
                break;
            case DUPLEX_SERVER:
                if (servers != null) {
                    if (servers.length > 0) {
                        servers[0].sendAllClients(value);
                    }
                }
                break;
            case MULTI_SERVER:
                if ((pIndexPort >= 0) && (pIndexPort <= 1)) {
                    if (servers != null) {
                        if (servers.length > pIndexPort) {
                            servers[pIndexPort].sendAllClients(value);
                        }
                    }
                } else {

                    servers[indexPort].sendAllClients(value);

                    indexPort++;
                    if (indexPort > servers.length - 1) {
                        indexPort = 0;
                    }
                }

                break;
            case SINGLE4:
                //clients[0].sendData(value);
                if ((pIndexPort >= 0) && (pIndexPort <= 1)) {

                    if (clients[pIndexPort].getSockState()) {
                        clients[pIndexPort].sendData(value);
                    }

                } else {

                    if (clients[indexPort].getSockState()) {
                        clients[indexPort].sendData(value);
                    } else {
                        if (clients[(indexPort == 0) ? 1 : 0].getSockState()) {
                            clients[(indexPort == 0) ? 1 : 0].sendData(value);
                        }
                    }
                    indexPort++;
                    if (indexPort > 1) {
                        indexPort = 0;
                    }
                }

                break;
        }
    }

    public void setIsoCfg(cfgNode pisoCfg) {
        isoCfg = pisoCfg;
    }

    public void setInstituttionIndex(int pinstituttionIndex) {
        instituttionIndex = pinstituttionIndex;
    }

    public int getInstituttionIndex() {
        return instituttionIndex;
    }

    public void setIncommingqueue(systemMessageQueue pincommingqueue) {
        this.incommingqueue = pincommingqueue;
    }

    public boolean checkConectionState() {
        if (connectionStop) {
            return false;
        }
        try {
            switch (cnnMode) {
                case DUPLEX_CLIENT:
                    return clients[0].getSockState();

                case DUPLEX_SERVER:
                    return servers[0].hasClients();

                case MULTI_SERVER:
                    boolean isOK = false;
                    if (servers != null) {
                        if (servers.length > 0) {
                            for (int i = 0; i < servers.length; i++) {
                                isOK = isOK | servers[i].hasClients();
                            }

                        }
                    }
                    return isOK;

                case SINGLE4:
                    return clients[0].getSockState() || clients[1].getSockState();

            }
        } catch (Exception ex) {
            return false;
        }
        return true;
    }

    public String getInstituttionCode() {
        return instituttionCode;
    }

    public void setInstituttionCode(String pinstituttionCode) {
        this.instituttionCode = pinstituttionCode;
    }

    public void setInstData(iqueryactionable pinstData) {
        this.instData = pinstData;
    }

    public void refresh() {
        cnnMode = instData.getConnMode();
        switch (cnnMode) {
            case DUPLEX_CLIENT:
                try {
                    if (clients != null) {
                        if (clients.length > 0) {

                            if (!clients[0].getSockState()) {
                                clients[0].close();
                                initDuplexClient();
                                clients[0].start();
                            }
                        } else {
                            CommonLib.PrintScreen(systemGlobal, String.format("Connection profile: %s equals sezo", String.valueOf(cnnMode)), showLogEnum.DETAILMODE);
                        }
                    } else {
                        CommonLib.PrintScreen(systemGlobal, String.format("Connection profile: %s NULL", String.valueOf(cnnMode)), showLogEnum.DETAILMODE);
                    }
                } catch (Exception ex) {
                }

                break;
            case DUPLEX_SERVER:

                break;
            case SINGLE4:
                try {
                    if (clients != null) {
                        if (clients.length > 1) {
                            if (!clients[0].getSockState()) {
                                clients[0].close();
                                restartSingle4Client(0);
                                clients[0].start();
                            }
                            if (!clients[1].getSockState()) {
                                clients[1].close();
                                restartSingle4Client(1);
                                clients[1].start();
                            }
                        }
                    }
                } catch (Exception ex) {
                }
                break;
        }
    }

    public String getTrace() {
        String connTrace = "";
        switch (cnnMode) {
            case DUPLEX_CLIENT:
                connTrace = String.format("INST: %s \t CnnType: %s \t CnnSTT: %s \tSTT: %s", instituttionCode, String.valueOf(cnnMode), checkConectionState(), getSystemStateDetail());

                break;
            case DUPLEX_SERVER:
                connTrace = String.format("INST: %s \t CnnType: %s \t CnnSTT: %s \tSTT: %s", instituttionCode, String.valueOf(cnnMode), checkConectionState(), getSystemStateDetail());
                break;
            case MULTI_SERVER:
                String serverSocket = "";
                for (int i = 0; i < servers.length; i++) {
                    serverSocket += servers[i].getPort() + ": " + servers[i].totalClients() + "cs\t ";
                }
                connTrace = String.format("INST: %s \t CnnType: %s \t %s STT: %s", instituttionCode, String.valueOf(cnnMode), serverSocket, getSystemStateDetail());
                break;
            case SINGLE4:
                connTrace = String.format("INST: %s \t CnnType: %s \t SPORT1: %s, %s cs \t SPORT2: %s, %s cs \t PORT1: %s \t PORT2: %s \tSTT: %s", instituttionCode, String.valueOf(cnnMode), servers[0].getPort(), servers[0].totalClients(), servers[1].getPort(), servers[1].totalClients(), clients[0].getSockState(), clients[1].getSockState(), getSystemStateDetail());

                break;
        }
        return connTrace;
    }

    public void stopConnection() {
        connectionStop = true;

        switch (cnnMode) {
            case DUPLEX_CLIENT:
                try {
                    if (clients != null) {
                        if (clients.length > 0) {
                            if (clients[0].getSockState()) {
                                clients[0].close();

                            }
                        }
                    }
                } catch (Exception ex) {
                }

                break;
            case DUPLEX_SERVER:
                try {
                    if (servers != null) {
                        if (servers.length > 0) {

                            /*if (servers[0].get()) {
                             clients[0].close();

                             }*/
                        }
                    }
                } catch (Exception ex) {
                }
                break;
        }
    }

    public void startConnection() {
        connectionStop = false;

        refresh();
    }

    public String getSystemStateDetail() {
        String rs = "";
        if (!systemState.isEmpty()) {
            for (int i = 0; i < systemState.size(); i++) {
                rs += "," + systemState.keySet().toArray()[i].toString();
            }
        }
        return rs;
    }

    /**
     * Check system ready state to process message when system is not ready
     * state, system is automatic response a message which RC=05
     *
     * @return true if system is ready to process message
     */
    public boolean checkStatus() {//true is ready, false is not ready
        return systemState.isEmpty() && checkConectionState() && (!connectionStop);
    }

    public void clearState(String key) {
        if (systemState.containsKey(key)) {
            systemState.remove(key);
        }
    }

    //Current support State: HSM Connection, Communication, Database and LogicState
    //HSM: HSM Connection
    //Sockets:
    //DB:
    //Status: (sign-off message)
    //ZPK_IN ZPK_OUT TAK_IN TAK_OUT: Key Echange error
    //BIN, SIGNOFF
    public void addSystemState(String key, String value) {
        if (!systemState.containsKey(key)) {
            switch (cnnMode) {
                case SINGLE4:
                    if (!checkConectionState()) {
                        systemState.put(key, value);
                    }
                    break;
                default:
                    systemState.put(key, value);
            }

        }
    }
}
