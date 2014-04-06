/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package processing;

import hsm.hsmCmdObj;
import iso8583.IsoMessage;
import iso8583.IsoMessageType;
import iso8583.msgSecurity;
import iss.showLogEnum;
import java.util.concurrent.ConcurrentHashMap;
import lib.CommonLib;
import lib.msgSecurityEnum;

import lib.secObjInfo;
import unisim201401.systemLoader;

/**
 *
 * @author minhdbh
 */
public class secObjQueue {

    ConcurrentHashMap<Integer, secObjInfo> objSecurityQueue;
    systemMessageSecurityQueue smSecQueue;
    systemMessageFlowControl msgFlowControl;
    unisim201401.systemLoader systemGlobalInfo;
    hsmCommandQueue hsmQueue;

    public void setHsmQueue(hsmCommandQueue hsmQueue) {
        this.hsmQueue = hsmQueue;
    }

    public secObjQueue() {
        objSecurityQueue = new ConcurrentHashMap<>();
    }

    public void addSecObj(secObjInfo newSecObj) {
        objSecurityQueue.put(CommonLib.valueOf(newSecObj.getHsmCommnadID()), newSecObj);
        CommonLib.PrintScreen(systemGlobalInfo, String.format("Sec Obj Queue add: CMD ID %s, MSG ID  %s, Type %s, zone %s",newSecObj.getHsmCommnadID(),newSecObj.getMsgID(),String.valueOf(newSecObj.getTypeOfSec()),newSecObj.getdZone()), showLogEnum.DETAILMODE);
    }

    public void setSmSecQueue(systemMessageSecurityQueue smSecQueue) {
        this.smSecQueue = smSecQueue;
    }

    public void setSystemGlobalInfo(systemLoader systemGlobalInfo) {
        this.systemGlobalInfo = systemGlobalInfo;
    }

    public systemMessageFlowControl getMsgFlowControl() {
        return msgFlowControl;
    }

    public void setMsgFlowControl(systemMessageFlowControl msgFlowControl) {
        this.msgFlowControl = msgFlowControl;
    }
    
    

    public secObjInfo checkSecQueue(msgSecurity pSec) {
        secObjInfo rs = null;
        if (objSecurityQueue.keySet().contains(pSec.getHsmID())) {
            return objSecurityQueue.get(pSec.getHsmID());
        }
        return rs;
    }

    public void removeSecObj(int pKeyID) {
        objSecurityQueue.remove(pKeyID);
    }

    public boolean processSecObj(msgSecurity pSec) {

        secObjInfo secInQueue = checkSecQueue(pSec);
        if (secInQueue != null) {
            objSecurityQueue.remove(CommonLib.valueOf(secInQueue.getHsmCommnadID()));
            IsoMessage msgSecCheck = smSecQueue.checkMessage(secInQueue);
            try {
                if (msgSecCheck != null) {


                    if (CommonLib.valueOf(pSec.getHsmErrCode()) == 0) {
                        switch (pSec.getMsgSecType()) {
                            case IN_NEED_OF_MACVER:
                            case IN_NEED_OF_MACVER_MD5:
                            case NET_TAK_TRANSLATE_ZMK_LMK:
                            case NET_ZPK_TRASLATE_ZMK_LMK:



                            case IN_NEED_OF_MACGEN:
                            case IN_NEED_GEN_PIN_ZPK:
                            case IN_NEED_OF_PIN:
                            case NET_TAK_GENERATE_ZMK:
                            case NET_ZPK_GENERATE_ZMK:

                            case IN_NEED_OF_MACGEN_MD5:
                                smSecQueue.removeMessage(secInQueue.getMsgID());
                                IsoMessage updatedMsg = systemGlobalInfo.getSecurityUtils(msgSecCheck.getDesInterfaceCode()).updateSecurity(msgSecCheck, pSec);
                                msgFlowControl.enqueueMessage(updatedMsg);
                                break;
                            case IN_NEED_GEN_PIN:
                                secObjInfo newSecReq = new secObjInfo(secInQueue);
                                newSecReq.setHsmCommnadID(CommonLib.getHSMCommandID());
                                newSecReq.setTypeOfSec(msgSecurityEnum.IN_NEED_GEN_PIN_ZPK);
                                newSecReq.setFields(new String[]{pSec.getHSMReturnValue()});
                                newSecReq.setdZone(secInQueue.getdZone());
                                objSecurityQueue.put(CommonLib.valueOf(newSecReq.getHsmCommnadID()), newSecReq);
                                hsmCmdObj cmdHsm = new hsmCmdObj();
                                cmdHsm.setHsmCommandID(CommonLib.valueOf(newSecReq.getHsmCommnadID()));
                                cmdHsm.setMsgType(newSecReq.getTypeOfSec());
                                cmdHsm.setCommandHSM(systemGlobalInfo.getSecurityUtils(msgSecCheck.getDesInterfaceCode()).getSecCommand(msgSecCheck, newSecReq).getCommandHSM());
                                hsmQueue.addNewCmd(cmdHsm);


                                break;

                        }

                    } else {
                        CommonLib.PrintScreen(systemGlobalInfo, "ERR HQP: HSM ID " + pSec.getHsmID(), showLogEnum.DETAILMODE);
                        String errCode = "05";


                        if (CommonLib.valueOf(pSec.getHsmErrCode()) > 0) {
                            switch (pSec.getMsgSecType()) {
                                case IN_NEED_OF_MACVER:
                                    errCode = "30";
                                    break;
                                case IN_NEED_OF_MACGEN:
                                    errCode = "05";
                                    break;
                                case IN_NEED_OF_PIN:
                                    errCode = "55";
                                default:
                                    errCode = "96";
                            }
                        }
                        switch (CommonLib.valueOf(msgSecCheck.getField(0))) {
                            case 200:
                                //msgSecCheck = systemGlobalInfo.getReplyMsgServiceByCode(msgSecCheck.getSourceInterfaceCode()).makeAutoResponseMsg(msgSecCheck, errCode);
                                //msgSecCheck.setSecRequest(systemGlobalInfo.getInstitutionData(msgSecCheck.getDesInterfaceCode()).getProcessingService().getSecurityQueueResponseFinacial(msgSecCheck));
                                break;

                            case 800:
                            case 820:
                            case 821:
                                // msgSecCheck = systemGlobalInfo.getReplyMsgServiceByCode(msgSecCheck.getSourceInterfaceCode()).makeAutoResponseMsg(msgSecCheck, errCode);
                                //  msgSecCheck.setSecRequest(systemGlobalInfo.getInstitutionData(msgSecCheck.getDesInterfaceCode()).getProcessingService().getSecurityQueue(msgSecCheck));
                                break;
                            case 420:
                            case 421:
                            case 430:
                            case 210:
                            default:
                            //Drop message
                            // msgSecCheck.setMsgType(IsoMessageType.DROP);
                            // CommonLib.PrintScreen(String.format("ERR HQP (%s) - %s (%s): %s ", institutionName, "DROP", String.valueOf(mSec.getMsgSecType()), msgSecCheck.getTraceInfo()));

                        }

                    }
                    if (msgSecCheck.getMsgType() != IsoMessageType.DROP) {
                        // msgFlowControlQueue.enqueueMessage(msgSecCheck);
                    }
                }

            } catch (Exception ex) {
                CommonLib.PrintScreen(systemGlobalInfo, String.format("ERR HQP : %s", ex.getMessage()),showLogEnum.DETAILMODE);
            }
        } else {
        }

        return true;
    }
}
