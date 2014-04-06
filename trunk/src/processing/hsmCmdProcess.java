/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package processing;

import connection.hsmConnections;
import exceptionshandle.bicsexception;
import hsm.hsmCmdObj;
import iss.showLogEnum;

import java.util.logging.Level;
import java.util.logging.Logger;
import lib.CommonLib;
import unisim201401.systemLoader;

/**
 *
 * @author minhdbh
 */
public class hsmCmdProcess extends Thread {

    private hsmConnections hsmCnns;
    private hsmCommandQueue cmdQueue;
    private systemLoader systemGlobal;

    public void setSystemGlobal(systemLoader systemGlobal) {
        this.systemGlobal = systemGlobal;
    }

    public hsmCmdProcess() {
    }

    public void setHsmCnns(hsmConnections hsmCnns) {
        this.hsmCnns = hsmCnns;
    }

    public void setCmdQueue(hsmCommandQueue cmdQueue) {
        this.cmdQueue = cmdQueue;
    }

    public void run() {

        while (true) {
            try {
                if (cmdQueue != null) {

                    hsmCmdObj cmd = cmdQueue.peekCmd();

                    if (cmd != null) {
                        CommonLib.PrintScreen(systemGlobal, String.format("Peek HSM ID: %s, TYPE: %s Cmd: %s", cmd.getCommandHSM(), String.valueOf(cmd.getMsgType()), cmd.getCommandHSM()), showLogEnum.DEFAULT);
                        System.out.println(hsmCnns.getCnnStatus());

                        hsmCnns.sendData(cmd.getCommandHSM());

                    }
                }
            } catch (Exception ex) {
                Logger.getLogger(hsmCmdProcess.class.getName()).log(Level.SEVERE, null, ex);
            }
            try {
                Thread.sleep(10);
            } catch (Exception ex1) {
            }
        }
    }
}
