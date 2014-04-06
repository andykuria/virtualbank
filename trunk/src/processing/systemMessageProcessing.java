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
                    if (DateUtils.DateDiff(DateTimeEnum.SECOND, imsg.getReceiveDatetime(), DateUtils.getDate()) >= systemGlobalInfo.getSystemConfig().getIntValue("GLOBAL", "QOS")) {
                        CommonLib.PrintScreen(systemGlobalInfo, "ERR SMP Drop message - time out: " + imsg.getTraceInfo(), showLogEnum.DETAILMODE);

                    } else {

                        switch (imsg.getMsgType()) {
                            case REQUEST:
                                routingtable rt = new routingtable(systemGlobalInfo.getcfgRouting());
                                switch (systemGlobalInfo.getRoutingType(imsg.getSourceInterfaceCode())) {
                                    case PAN:
                                        imsg.setDesInterfaceCode(rt.getRoutingByPan(imsg.getField(2)));
                                        break;
                                    case PORT:
                                        imsg.setDesInterfaceCode(rt.getRoutingByPort(imsg.getSourceInterfaceCode()));
                                        break;
                                }

                                imsg.setSecRequest(systemGlobalInfo.getSecurityUtils(imsg.getDesInterfaceCode()).getSecurityList(imsg));


                                break;
                            case RESPONSE:

                                break;
                            case NETWORK_REQUEST:
                                imsg.setSecRequest(systemGlobalInfo.getSecurityUtils(imsg.getSourceInterfaceCode()).getSecurityList(imsg));
                                break;
                            case NETWORK_RESPONSE:
                                break;
                            default:

                        }
                        msgFlowControlQueue.enqueueMessage(imsg);

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
