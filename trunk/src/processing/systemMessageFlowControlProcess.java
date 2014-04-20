/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package processing;

import hsm.pinInfo;
import iso8583.IsoMessage;

import iso8583.isolib;
import iss.showLogEnum;

import lib.CommonLib;

import lib.secObjInfo;

import unisim201401.systemLoader;

/**
 *
 * @author kt1
 */
public class systemMessageFlowControlProcess extends Thread {

    private systemMessageFlowControl mQueue;
    private systemLoader systemGlobalInfo;
    private systemMessageQueue outgoingQueue;

    private hsmSecQueue mhsmqueue;

    public void setSystemGlobalInfo(systemLoader psystemGlobalInfo) {
        this.systemGlobalInfo = psystemGlobalInfo;
    }

    public void setInstitutionQueue(systemMessageQueue pinstitutionQueue) {
        this.outgoingQueue = pinstitutionQueue;
    }

    public systemMessageFlowControlProcess(systemMessageFlowControl pmQueue) {
        this.mQueue = pmQueue;
    }

    public void setSystemControlQueue(systemMessageFlowControl pQueue) {
        this.mQueue = pQueue;
    }

    public void sethsmqueue(hsmSecQueue pmhsmqueue) {
        this.mhsmqueue = pmhsmqueue;
    }

    @Override
    public void run() {
        while (true) {
            if (mQueue != null) {

                IsoMessage msg = mQueue.peekMessage();

                if (msg != null) {
                    //if (DateUtils.DateDiff(DateTimeEnum.SECOND, msg.getReceiveDatetime(), DateUtils.getDate()) >= systemGlobalInfo.getSystemConfig().getIntValue("SYSTEM", "QOS")) {
                    if (isolib.isDropState(msg, systemGlobalInfo.getSystemConfig().getIntValue("GLOBAL", "QOS"))) {
                        //msgStateDone.add(String.valueOf(msg.getMessageInID()));
                        CommonLib.PrintScreen(systemGlobalInfo, "ERR SMFCP: Drop message " + msg.getTraceInfo(), showLogEnum.DETAILMODE);

                    } else {
                        CommonLib.PrintScreen(systemGlobalInfo, String.format("SMFCP - SECURITY: %s", msg.getSecurityInfo()), showLogEnum.DETAILMODE);

                        secObjInfo newSecReq = msg.getSecRequest();
                        try {
                            if (newSecReq == null) {

                                CommonLib.PrintScreen(systemGlobalInfo, "SMFCP - DONE: " + msg.getTraceInfo(), showLogEnum.DETAILMODE);
                                outgoingQueue.add(msg);

                            } else {
                                CommonLib.PrintScreen(systemGlobalInfo, String.format("SMFCP - %s: %s", String.valueOf(newSecReq.getTypeOfSec()), msg.getTraceInfo()), showLogEnum.DEFAULT);

                                switch (newSecReq.getTypeOfSec()) {
                                    case AUTO_RESPONSE:
                                        if (msg.getSourceInterfaceCode().equals("SIMUI")) {
                                            msg = systemGlobalInfo.getIssResponse(msg.getDesInterfaceCode()).getResponse(msg);
                                        } else {
                                            msg = systemGlobalInfo.getIssResponse(msg.getSourceInterfaceCode()).getResponse(msg);
                                        }
                                        mQueue.enqueueMessage(msg);
                                        break;

                                    case MAKE_RESPONSE:
                                        switch (msg.getMsgType()) {
                                            case NETWORK_REQUEST:
                                                msg = systemGlobalInfo.getIssResponse(msg.getSourceInterfaceCode()).getResponse(msg);
                                                mQueue.enqueueMessage(msg);
                                                break;
                                            case REQUEST:
                                                msg = systemGlobalInfo.getIssResponse(msg.getDesInterfaceCode()).getResponse(msg);
                                                mQueue.enqueueMessage(msg);
                                                break;
                                        }

                                        break;

                                    case IN_NEED_OF_MACVER:
                                    case IN_NEED_OF_MACGEN:
                                    case IN_NEED_OF_PIN:
                                    case NET_TAK_GENERATE_ZMK:

                                    case NET_TAK_TRANSLATE_ZMK_LMK:
                                    case NET_ZPK_GENERATE_ZMK:
                                    case NET_ZPK_TRASLATE_ZMK_LMK:

                                        mhsmqueue.enqueue(msg);

                                        break;
                                    case IN_NEED_GEN_PIN:
                                        String pinKeyValue = msg.getField(52) + msg.getDesInterfaceCode();
                                        pinInfo pinCached = systemGlobalInfo.getPinMap().get(pinKeyValue.hashCode());
                                        if (pinCached != null) {
                                            if (pinCached.isPinBlock()) {
                                                msg.peekSecRequest();
                                                msg.setField(52, pinCached.getPinBlockInHex());
                                                mQueue.enqueueMessage(msg);
                                            } else {
                                                mhsmqueue.enqueue(msg);
                                            }
                                        } else {
                                            mhsmqueue.enqueue(msg);
                                        }
                                        break;

                                }
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                            CommonLib.PrintScreen(systemGlobalInfo, String.format("ERR SMFCP - Processing 1: %s \n\t\t %s", ex.getMessage(), msg.getTraceInfo()), showLogEnum.DETAILMODE);
                        }

                    }
                }
            }
            try {
                Thread.sleep(10);
            } catch (InterruptedException ex) {
                CommonLib.PrintScreen(systemGlobalInfo, String.format("ERR SMFCP - Processing 2: %s \n", ex.getMessage()), showLogEnum.DETAILMODE);
            }
        }

    }
}
