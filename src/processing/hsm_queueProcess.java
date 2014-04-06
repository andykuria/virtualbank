/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package processing;

import exceptionshandle.bicsexception;
import globalutils.LogActionEnum;
import hsm.hsmCmdObj;
import iso8583.IsoMessage;
import iso8583.IsoMessageType;
import iso8583.isolib;
import iso8583.msgSecurity;
import iso8583.msgSecurityCommand;
import iss.showLogEnum;
import lib.CommonLib;

import lib.secObjInfo;
import lib.securityLib;
import unisim201401.systemLoader;

/**
 *
 * @author kt1
 */
public class hsm_queueProcess extends Thread {

    private systemLoader systemGlobalInfo;
    private systemMessageSecurityQueue mSecurityQueue;
    private hsmSecQueue mhsmqueue;
    private hsmCommandQueue cmdHsmQueue;
    private secObjQueue mObjSecQueue;

    public void setSystemGlobalInfo(systemLoader psystemGlobalInfo) {
        this.systemGlobalInfo = psystemGlobalInfo;
    }

    public void sethsmqueue(hsmSecQueue pmhsmqueue) {
        this.mhsmqueue = pmhsmqueue;
    }

    public void setSecurityQueue(systemMessageSecurityQueue pmSecurityQueue) {
        this.mSecurityQueue = pmSecurityQueue;
    }

    public void setMhsmqueue(hsmSecQueue mhsmqueue) {
        this.mhsmqueue = mhsmqueue;
    }

    public void setmObjSecQueue(secObjQueue mObjSecQueue) {
        this.mObjSecQueue = mObjSecQueue;
    }

    public void setCmdHsmQueue(hsmCommandQueue cmdHsmQueue) {
        this.cmdHsmQueue = cmdHsmQueue;
    }
    
    

    @Override
    public void run() {
        while (true) {
            try {
                if (mhsmqueue != null) {

                    IsoMessage msg = mhsmqueue.peekMessage();

                    if (msg != null) {
                        //if (DateUtils.DateDiff(DateTimeEnum.SECOND, msg.getReceiveDatetime(), DateUtils.getDate()) >= systemGlobalInfo.getSystemConfig().getIntValue("SYSTEM", "QOS")) {
                        if (isolib.isDropState(msg, systemGlobalInfo.getSystemConfig().getIntValue("GLOBAL", "QOS"))) {
                            //msgStateDone.add(String.valueOf(msg.getMessageInID()));
                            CommonLib.PrintScreen(systemGlobalInfo, "ERR SMFCP: Drop message " + msg.getTraceInfo(),showLogEnum.DETAILMODE);

                        } else {
                            CommonLib.PrintScreen(systemGlobalInfo, "HSM Obj queue process "+ msg.getTraceInfo(), showLogEnum.DEFAULT);
                            secObjInfo newSecReq = msg.peekSecRequest();
                            mSecurityQueue.addMessage(msg);

                            secObjInfo mObj = new secObjInfo();
                            mObjSecQueue.addSecObj(newSecReq);

                            
                            hsmCmdObj cmdHsm = new hsmCmdObj();
                            cmdHsm.setHsmCommandID(CommonLib.valueOf(newSecReq.getHsmCommnadID()));
                            cmdHsm.setMsgType(newSecReq.getTypeOfSec());
                            cmdHsm.setCommandHSM(systemGlobalInfo.getSecurityUtils(msg.getDesInterfaceCode()).getSecCommand(msg, newSecReq).getCommandHSM());
                            cmdHsmQueue.addNewCmd(cmdHsm);
                        }
                    }
                }
            } catch (Exception ex1) {
            }

            try {
                Thread.sleep(10);
            } catch (Exception ex) {
            }
        }
    }
}
