/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package connection;

import exceptionshandle.bicserrcode;
import exceptionshandle.bicsexception;
import hsm.hsmProcess;
import iss.showLogEnum;
import lib.CommonLib;
import processing.secObjQueue;
import processing.systemMessageSecurityQueue;
import unisim201401.systemLoader;

/**
 *
 * @author kt1
 */
public class hsmConnections {

    private int numberCnn;
    private hsmProcess[] hsmConnectors;
    private int iCnnUse;
    private String server = "";
    private int port = 0;
    private secObjQueue objSecQueue;

     private systemLoader systemGlobal;

    public void setSystemGlobal(systemLoader systemGlobal) {
        this.systemGlobal = systemGlobal;
    }
    public void hsmConnections(int pnumberCnn) {
        this.numberCnn = pnumberCnn;
        iCnnUse = 0;
    }

    public void setSystemSecQueue(secObjQueue systemSecQueue) {
        this.objSecQueue = systemSecQueue;
    }

    public void hsmConnections(String pServer, int pPort, int pnumberCnn) {
        this.numberCnn = pnumberCnn;
        this.server = pServer;
        this.port = pPort;
        iCnnUse = 0;
    }

    public hsmConnections() {
        numberCnn = 3;
        iCnnUse = 0;
    }

    public int getNumberCnn() {
        return numberCnn;
    }

    public void setNumberCnn(int numberCnn) {
        this.numberCnn = numberCnn;
    }

    public int getiCnnUse() {
        return iCnnUse;
    }

    public void setiCnnUse(int iCnnUse) {
        this.iCnnUse = iCnnUse;
    }

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void initHSM() {
        hsmConnectors = new hsmProcess[numberCnn];
        for (int i = 0; i < numberCnn; i++) {
            hsmConnectors[i] = new hsmProcess();
            hsmConnectors[i].setServer(server);
            hsmConnectors[i].setPort(port);
            hsmConnectors[i].setInstitutionIndex(i + 1);
            hsmConnectors[i].setQueueSecurity(objSecQueue);
            hsmConnectors[i].setSystemGlobal(systemGlobal);
            hsmConnectors[i].start();
        }
    }

    public String getCnnStatus() {
        String CnnStt = "";
        try {
            CnnStt = "   Totals HSM Cnns: " + numberCnn;
            for (int ihsmCnn = 0; ihsmCnn < numberCnn; ihsmCnn++) {
                CnnStt += String.format("   " + "hsm[%d]: %s", (ihsmCnn + 1), hsmConnectors[ihsmCnn].isAlive());
                //CnnStt +=  String.valueOf(dbConn[idbCnn].isIsAlive())+"\t";
            }
        } catch (Exception ex) {
        }
        return CnnStt;
    }

    public void sendData(String value) throws bicsexception {
        boolean isLoop = false;
        int startCnn = iCnnUse;
        hsmProcess hsmCnn;
        int icount = 1;
        while (!isLoop) {

            if (iCnnUse >= numberCnn) {
                iCnnUse = 1;
            } else {
                iCnnUse++;
            }

            if (hsmConnectors[iCnnUse - 1].isAlive()) {
                CommonLib.PrintScreen(systemGlobal, String.format("HSM- choice connection index %d", iCnnUse),showLogEnum.DETAILMODE);
                hsmConnectors[iCnnUse - 1].sendData(value);
                isLoop=true;
            }
            icount++;
            if (iCnnUse == startCnn) {

                isLoop = true;
            }
            if (icount > numberCnn) {
                isLoop = true;
            }
            if (isLoop) {
                return;
            }
        }
    }
    
}
