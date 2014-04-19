/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package processing;

import datamanager.routingtable;
import globalutils.LogActionEnum;

import iso8583.IsoMessage;

import iso8583.IsoMessageType;
import iss.showLogEnum;
import java.util.concurrent.ConcurrentLinkedQueue;

import lib.CommonLib;
import lib.DateTimeEnum;
import lib.DateUtils;

import lib.msgSecurityEnum;
import lib.secObjInfo;
import unisim201401.systemLoader;

/**
 *
 * @author minhdbh
 */
public class systemMessageProcessing extends Thread {

    private systemLoader systemGlobalInfo;
    private systemMessageQueue institutionQueue;
    private systemMessageFlowControl msgFlowControlQueue;

    public void setSystemGlobalInfo(systemLoader psystemGlobalInfo) {
        this.systemGlobalInfo = psystemGlobalInfo;
    }

    public void setMsgFlowControlQueue(systemMessageFlowControl pmsgFlowControlQueue) {
        this.msgFlowControlQueue = pmsgFlowControlQueue;
    }

    public void setInstitutionQueue(systemMessageQueue pinstitutionQueue) {
        this.institutionQueue = pinstitutionQueue;
    }

    @Override
    public void run() {

        CommonLib.PrintScreen(systemGlobalInfo, "System Message process is started", showLogEnum.DETAILMODE);
        while (true) {
            try {
                IsoMessage imsg = institutionQueue.peekMessage();
                if (imsg != null) {
                    if (DateUtils.DateDiff(DateTimeEnum.MILISECOND, imsg.getReceiveDatetime(), DateUtils.getDate()) >= systemGlobalInfo.getSystemConfig().getIntValue("GLOBAL", "QOS") * 1000) {
                        CommonLib.PrintScreen(systemGlobalInfo, "ERR SMP Drop message - time out: " + imsg.getTraceInfo(), showLogEnum.DETAILMODE);

                    } else {

                        switch (imsg.getMsgType()) {
                            case REQUEST:
                                if (imsg.getSourceInterfaceCode().equals("SIMUI")) {

                                } else {
                                    routingtable rt = new routingtable(systemGlobalInfo.getcfgRouting());
                                    switch (systemGlobalInfo.getRoutingType(imsg.getSourceInterfaceCode())) {
                                        case PAN:
                                            imsg.setDesInterfaceCode(rt.getRoutingByPan(imsg.getField(2)));
                                            break;
                                        case PORT:
                                            imsg.setDesInterfaceCode(rt.getRoutingByPort(imsg.getSourceInterfaceCode()));
                                            break;
                                    }
                                }
                                if (systemGlobalInfo.getINFSecurityUtils(imsg.getDesInterfaceCode()) == null) {
                                    secObjInfo newSec = new secObjInfo(msgSecurityEnum.MAKE_RESPONSE);
                                    newSec.setMsgID(imsg.getSeqID());
                                    newSec.setsZone(imsg.getDesInterfaceCode());
                                    newSec.setdZone(imsg.getSourceInterfaceCode());
                                    imsg.addSecRequest(newSec);

                                } else {
                                    imsg.setSecRequest(systemGlobalInfo.getINFSecurityUtils(imsg.getDesInterfaceCode()));
                                }

                                msgFlowControlQueue.enqueueMessage(imsg);
                                break;
                            case RESPONSE:
                                IsoMessage origTranx = systemGlobalInfo.getOriginalMap().get(imsg.getHashCode());
                                if (origTranx != null) {
                                    systemGlobalInfo.getOriginalMap().remove(imsg.getHashCode());
                                    if (origTranx.getSourceInterfaceCode().toUpperCase().equals("SIMUI")) {
                                        CommonLib.PrintScreen(systemGlobalInfo, "Rev response: " + imsg.printedMessage(), showLogEnum.DEFAULT);
                                    } else {
                                        imsg.setDesInterfaceCode(origTranx.getSourceInterfaceCode());
                                        imsg.setSecRequest(systemGlobalInfo.getINFSecurityUtils(imsg.getDesInterfaceCode()));
                                        msgFlowControlQueue.enqueueMessage(imsg);
                                    }
                                } else {
                                    CommonLib.PrintScreen(systemGlobalInfo, "Rev response (DROP - NOT HAVE ORIG TRANX): " + imsg.printedMessage(), showLogEnum.DEFAULT);
                                }

                                break;
                            case NETWORK_REQUEST:
                                imsg.setSecRequest(systemGlobalInfo.getSecurityUtils(imsg.getSourceInterfaceCode()));
                                msgFlowControlQueue.enqueueMessage(imsg);
                                break;
                            case NETWORK_RESPONSE:
                                break;
                            default:
                                msgFlowControlQueue.enqueueMessage(imsg);

                        }

                    }

                }
            } catch (Exception ex) {
                CommonLib.PrintScreen(systemGlobalInfo, "ERR SMP - PROCESS: " + ex.getMessage(), showLogEnum.DETAILMODE);

            }
            try {
                Thread.sleep(10);
            } catch (InterruptedException ex) {
                CommonLib.PrintScreen(systemGlobalInfo, "ERR SMP: " + ex.getMessage(), showLogEnum.DETAILMODE);

            }

        }
    }
}
