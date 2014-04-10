/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package processing;

import ginterface.ithreadSequence;
import globalutils.LogActionEnum;

import iso8583.IsoMessage;
import iss.showLogEnum;

import java.util.logging.Level;
import java.util.logging.Logger;
import lib.CommonLib;
import lib.DateTimeEnum;
import lib.DateUtils;
import lib.instBOX;
import unisim201401.systemLoader;

/**
 *
 * @author minhdbh
 */
public class systemMessageSender extends Thread implements ithreadSequence {

    private systemMessageQueue institutionQueue;
    private systemLoader systemGlobalInfo;
    private Thread parentThread = null;
    private boolean threadStatus = false;

    public boolean getStatus() {
        return threadStatus;
    }

    public void setParentThread(Thread pThread) {
        this.parentThread = pThread;
    }

    public void setInstitutionQueue(systemMessageQueue pinstitutionQueue) {
        this.institutionQueue = pinstitutionQueue;
    }

    public void setSystemGlobalInfo(systemLoader psystemGlobalInfo) {
        this.systemGlobalInfo = psystemGlobalInfo;
    }

    public systemMessageSender() {
    }

    public void run() {
        synchronized (this) {
            while (true) {
                if (parentThread != null) {
                    if (!((ithreadSequence) parentThread).getStatus()) {
                        try {
                            Thread.sleep(100);

                        } catch (InterruptedException ex) {
                            CommonLib.PrintScreen(systemGlobalInfo, "Couldn't do waiting Sending process", showLogEnum.DETAILMODE);
                        }
                    } else {
                        break;
                    }
                } else {
                    break;
                }
            }

        }

        CommonLib.PrintScreen(systemGlobalInfo, "Sending process is started.", showLogEnum.DETAILMODE);
        threadStatus = true;

        while (true) {
            try {

                IsoMessage imsg = institutionQueue.peekMessage();
                if (imsg != null) {
                    if (DateUtils.DateDiff(DateTimeEnum.SECOND, imsg.getReceiveDatetime(), DateUtils.getDate()) >= systemGlobalInfo.getSystemConfig().getIntValue("GLOBAL", "QOS")) {
                        CommonLib.PrintScreen(systemGlobalInfo, "SMS: Drop message " + imsg.getTraceInfo(), showLogEnum.DETAILMODE);

                    } else {
                        if (imsg.getDesInterfaceCode().equals("SIMUI")) {
                            CommonLib.PrintScreen(systemGlobalInfo, "GET RESPONSE "+imsg.printedMessage(), showLogEnum.DEFAULT);

                        } else {
                            CommonLib.PrintScreen(systemGlobalInfo, "Mesage should send: " + imsg.printedMessage(), showLogEnum.DETAILMODE);
                            if (systemGlobalInfo.getConnectorByInstitution(imsg.getDesInterfaceCode()).checkConectionState()) {

                                //systemGlobalInfo.getConnectorByInstitution(imsg.getDesInterfaceCode()).sendData(imsg.toString());
                                imsg.setmsgHeader(systemGlobalInfo.getInstitutionData(imsg.getDesInterfaceCode()).getHeaderInfo());
                                imsg.setLineMode(systemGlobalInfo.getInstitutionData(imsg.getDesInterfaceCode()).getLineMode());
                                imsg.setIsoCfg(systemGlobalInfo.getIsoFormatByScope(systemGlobalInfo.getInstitutionDataConfig(imsg.getDesInterfaceCode()).getValue("SCOPE")));

                                CommonLib.PrintScreen(systemGlobalInfo, "SMS - Sent to socket: " + imsg.printedMessage(), showLogEnum.DEFAULT);
                                switch (CommonLib.valueOf(imsg.getField(0))) {
                                    case 200:
                                        systemGlobalInfo.getOriginalMap().add(imsg.getHashCode(), imsg);

//                                    if (imsg.getSourceInterfaceCode().toUpperCase().equals("SIMUI")) {
//                                        IsoMessage revMsg = systemGlobalInfo.getIssResponse(imsg.getDesInterfaceCode()).makeRevFromFin(imsg);
//                                        systemGlobalInfo.getReversalMap().add(revMsg.getHashCode(), revMsg);
//                                    }
                                        break;
                                }
                                switch (CommonLib.valueOf(imsg.getField(70))) {
                                    case 999:
                                        systemGlobalInfo.getConnectorByInstitution(imsg.getDesInterfaceCode()).sendData(imsg.toByte(), imsg.getPortIndex());
                                        break;
                                    default:
                                        systemGlobalInfo.getConnectorByInstitution(imsg.getDesInterfaceCode()).sendData(imsg.toByte(), -1);
                                }

//                            saveNetworkCfg(imsg);
                            }

                        }
                    }
                }

            } catch (Exception ex) {
                //ex.printStackTrace();
            }
            try {
                Thread.sleep(5);
            } catch (InterruptedException ex) {
                Logger.getLogger(systemMessageSender.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
    }
    /*
     private void saveNetworkCfg(IsoMessage pmsg) {
     switch (pmsg.getMsgType()) {
     case NETWORK:
     case NETWORK_SYSTEM_GEN:
     instBOX cfgValue = systemGlobalInfo.getConfigQueue().peekMessage(pmsg.getSeqID());

     if (cfgValue != null) {
     CommonLib.PrintScreen("SMS apply new config: " + cfgValue.toString());
     systemGlobalInfo.addLogData(LogActionEnum.RAP, "SMS apply new config: " + cfgValue.toString());
     if (!cfgValue.getInstCode().trim().equals("")) {
     System.out.println("SMS Apply config " + cfgValue.toString());
     switch (systemGlobalInfo.getInstitutionData(cfgValue.getInstCode()).getInstConfig().getIntValue("INSTITUTION", "KEY_TYPE")) {
     case 2:

     if (cfgValue.getBoxTAG().equals("ZPK") || cfgValue.getBoxTAG().equals("TAK")) {
     if (cfgValue.getBoxTAG().equals("ZPK")) {
     systemGlobalInfo.getInstitutionData(cfgValue.getInstCode()).getInstConfig().setValue("INSTITUTION", "ZPK2", systemGlobalInfo.getInstitutionData(cfgValue.getInstCode()).getInstConfig().getValue("INSTITUTION", "ZPK1"));
     systemGlobalInfo.getInstitutionData(cfgValue.getInstCode()).getInstConfig().setValue("INSTITUTION", "ZPK1", cfgValue.getBoxValue());
     systemGlobalInfo.getInstitutionData(cfgValue.getInstCode()).getInstConfig().saveCfg();
     } else {
     systemGlobalInfo.getInstitutionData(cfgValue.getInstCode()).getInstConfig().setValue("INSTITUTION", "TAK2", systemGlobalInfo.getInstitutionData(cfgValue.getInstCode()).getInstConfig().getValue("INSTITUTION", "TAK1"));
     systemGlobalInfo.getInstitutionData(cfgValue.getInstCode()).getInstConfig().setValue("INSTITUTION", "TAK1", cfgValue.getBoxValue());
     systemGlobalInfo.getInstitutionData(cfgValue.getInstCode()).getInstConfig().saveCfg();
     }

     } else {
     systemGlobalInfo.getInstitutionData(cfgValue.getInstCode()).getInstConfig().setValue("INSTITUTION", cfgValue.getBoxTAG(), cfgValue.getBoxValue());
     systemGlobalInfo.getInstitutionData(cfgValue.getInstCode()).getInstConfig().saveCfg();
     }
     break;
     default:
     systemGlobalInfo.getInstitutionData(cfgValue.getInstCode()).getInstConfig().setValue("INSTITUTION", cfgValue.getBoxTAG(), cfgValue.getBoxValue());
     systemGlobalInfo.getInstitutionData(cfgValue.getInstCode()).getInstConfig().saveCfg();

     }
     }
     }
     break;
     }
     }
    
     */
}
