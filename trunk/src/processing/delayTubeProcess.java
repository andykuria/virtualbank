/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package processing;

import hsm.hsmCmdObj;
import iso8583.IsoMessage;
import iss.showLogEnum;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import lib.CommonLib;
import lib.DateUtils;
import unisim201401.systemLoader;

/**
 *
 * @author netone
 */
public class delayTubeProcess extends Thread {

    delayTube tubeMessage;
    private systemLoader systemGlobal;

    public delayTubeProcess() {
    }

    public void setTubeMessage(delayTube tubeMessage) {
        this.tubeMessage = tubeMessage;
    }

    public void setSystemGlobal(systemLoader systemGlobal) {
        this.systemGlobal = systemGlobal;
        tubeMessage = this.systemGlobal.getDelayQueue();
    }

    public void run() {

        while (true) {
            try {
                if (tubeMessage != null) {
                    List<IsoMessage> msgInTub = tubeMessage.getMessages(DateUtils.getDate());
                    for (IsoMessage imsg : msgInTub) {
                        imsg.resetTime();
                        systemGlobal.getIcmQueue().add(imsg);
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
