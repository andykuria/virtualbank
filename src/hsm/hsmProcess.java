/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hsm;

import exceptionshandle.bicserrcode;
import exceptionshandle.bicsexception;
import ginterface.ithreadMonitor;
import ginterface.ithreadSequence;
import globalutils.threadTypeEnum;
import iso8583.msgSecurity;
import iss.showLogEnum;
import java.io.DataInputStream;


import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;


import lib.CommonLib;
import lib.msgSecurityEnum;
import processing.secObjQueue;
import unisim201401.systemLoader;

/**
 *
 * @author Administrator
 *
 */
public class hsmProcess extends Thread implements ithreadMonitor, ithreadSequence {

    private String server = "";
    private int port = 0;
    private DataOutputStream outgoingData;
    private DataInputStream incomingData;
    private Socket hsmClientSocket = null;
    private boolean isRunning = true;
    private int institutionIndex = -1;
    private Thread parentThread = null;
    private boolean threadStatus = false;
    private secObjQueue securityQueue;

    
     private systemLoader systemGlobal;

    public void setSystemGlobal(systemLoader systemGlobal) {
        this.systemGlobal = systemGlobal;
    }
    public void setQueueSecurity(secObjQueue pqueueSecurity) {
        this.securityQueue = pqueueSecurity;
    }

    public boolean getStatus() {
        return threadStatus;
    }

    public void setParentThread(Thread pThread) {
        this.parentThread = pThread;
    }

    public void setInstitutionIndex(int pinstitutionIndex) {
        this.institutionIndex = pinstitutionIndex;
    }

    public hsmProcess() {

        isRunning = true;
        setDaemon(true);
    }

    @Override
    public void run() {
        synchronized (this) {
            while (true) {
                if (parentThread != null) {
                    if (!((ithreadSequence) parentThread).getStatus()) {
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException ex) {
                            CommonLib.PrintScreen(systemGlobal, "Couldn't do waiting HSM process " + institutionIndex,showLogEnum.DETAILMODE);
                        }
                    } else {
                        break;
                    }
                } else {
                    break;
                }
            }
        }


        initSocket();
        CommonLib.PrintScreen(systemGlobal, " HSM process is started " + institutionIndex, showLogEnum.DETAILMODE);

        while (isRunning) {

            try {
                String commandResponse;
                int dataLen = -1;
                commandResponse = incomingData.readUTF();
                if (commandResponse != null) {
                    commandResponse = commandResponse.trim();
                    dataLen = commandResponse.length();
                    CommonLib.PrintScreen(systemGlobal, String.format(" HSM %d rev: ", institutionIndex) + commandResponse,showLogEnum.SIMPLEMODE);
                    while (dataLen > 0) {
                        while (commandResponse.length() > 0) {
                            msgSecurity mSec = new msgSecurity(ParseHsmMessage(commandResponse));


                            CommonLib.PrintScreen(systemGlobal, String.format("HSM (%s) REV\t  Type:%s  Value: %s  RC:%s  KCV: %s  from %s", institutionIndex + "", String.valueOf(mSec.getMsgSecType()), mSec.getHSMReturnValue(), mSec.getHsmErrCode(), mSec.getKeyCheckValue(), commandResponse),showLogEnum.DEFAULT);
                            if (mSec != null) {

                                securityQueue.processSecObj(mSec);
                            }


                            commandResponse = commandResponse.substring(mSec.getCommandLen()).trim();

                        }
                        commandResponse = incomingData.readUTF().trim();
                        dataLen = commandResponse.length();
                    }
                } else {
                    CommonLib.PrintScreen(systemGlobal, String.format("ERR HQP (%s): HSM connection is interrupted...", institutionIndex + 1), showLogEnum.DETAILMODE);
                    isRunning = false;

                }

                //Thread.sleep(GlobalObject.GW_SYS_Paras.getIntValue("SYSTEM","HSMProcessRunningInterval"));
            } catch (Exception ex) {
                isRunning = false;
                ex.printStackTrace();
            }
        }
    }

    private void initSocket() {
        try {
            System.out.printf(institutionIndex + " HSM Try to connect (%s,%d) \n", server, port);
            hsmClientSocket = new Socket(server, port);
            incomingData = new DataInputStream(hsmClientSocket.getInputStream());
            outgoingData = new DataOutputStream(hsmClientSocket.getOutputStream());
            System.out.printf(institutionIndex + " HSM connected to (%s,%d) at (%d)\n", server, port, hsmClientSocket.getLocalPort());
            isRunning = true;
            threadStatus = true;

            //Thread.sleep(GlobalObject.GW_SYS_Paras.getIntValue("SYSTEM","HSM_RUNNING_INTEVAL"));
        } catch (IOException ex) {
            isRunning = false;
        } catch (Exception ex) {
            isRunning = false;
        }
    }

    public void sendData(String value) throws bicsexception {
        try {
            outgoingData.writeUTF(value);
            outgoingData.flush();
        } catch (Exception ex) {
            isRunning = false;
            throw new bicsexception(String.format("HSM (%s): SOCKET ERROR  %s", institutionIndex + "", ex.getMessage()), bicserrcode.HSM_ERR);

        }
    }

    public void setServer(String value) {
        server = value;
    }

    public void setPort(int value) {
        port = value;
    }

    public msgSecurity ParseHsmMessage(String hsmMsg) {
        msgSecurity resHsmVal = new msgSecurity();
        try {

            resHsmVal.setMsgHeaderLen(hsmMsg.indexOf("CD"));

            if (resHsmVal.getMsgHeaderLen() < 0 || (resHsmVal.getMsgHeaderLen() >= 8)) {
                resHsmVal.setMsgHeaderLen(hsmMsg.indexOf("FB"));
            }
            if (resHsmVal.getMsgHeaderLen() < 0 || (resHsmVal.getMsgHeaderLen() >= 8)) {
                resHsmVal.setMsgHeaderLen(hsmMsg.indexOf("IB"));
            }
            if (resHsmVal.getMsgHeaderLen() < 0 || (resHsmVal.getMsgHeaderLen() >= 8)) {
                resHsmVal.setMsgHeaderLen(hsmMsg.indexOf("HB"));
            }
            if (resHsmVal.getMsgHeaderLen() < 0 || (resHsmVal.getMsgHeaderLen() >= 8)) {
                resHsmVal.setMsgHeaderLen(hsmMsg.indexOf("MH"));
            }
            if (resHsmVal.getMsgHeaderLen() < 0 || (resHsmVal.getMsgHeaderLen() >= 8)) {
                resHsmVal.setMsgHeaderLen(hsmMsg.indexOf("MJ"));
            }
            if (resHsmVal.getMsgHeaderLen() < 0 || (resHsmVal.getMsgHeaderLen() >= 8)) {
                resHsmVal.setMsgHeaderLen(hsmMsg.indexOf("ML"));
            }
            if (resHsmVal.getMsgHeaderLen() < 0 || (resHsmVal.getMsgHeaderLen() >= 8)) {
                resHsmVal.setMsgHeaderLen(hsmMsg.indexOf("MN"));
            }
            if (resHsmVal.getMsgHeaderLen() < 0 || (resHsmVal.getMsgHeaderLen() >= 8)) {
                resHsmVal.setMsgHeaderLen(hsmMsg.indexOf("A1"));
            }
            if (resHsmVal.getMsgHeaderLen() < 0 || (resHsmVal.getMsgHeaderLen() >= 8)) {
                resHsmVal.setMsgHeaderLen(hsmMsg.indexOf("M1"));
            }
            if (resHsmVal.getMsgHeaderLen() < 0 || (resHsmVal.getMsgHeaderLen() >= 8)) {
                resHsmVal.setMsgHeaderLen(hsmMsg.indexOf("M3"));
            }
            if (resHsmVal.getMsgHeaderLen() < 0 || (resHsmVal.getMsgHeaderLen() >= 8)) {
                resHsmVal.setMsgHeaderLen(hsmMsg.indexOf("BB"));
            }
            if (resHsmVal.getMsgHeaderLen() < 0 || (resHsmVal.getMsgHeaderLen() >= 8)) {
                resHsmVal.setMsgHeaderLen(hsmMsg.indexOf("JH"));
            }

            resHsmVal.setHsmID(Integer.parseInt(hsmMsg.substring(resHsmVal.getMsgHeaderLen() - 4, resHsmVal.getMsgHeaderLen())));
            String hsmCode = hsmMsg.substring(resHsmVal.getMsgHeaderLen(), resHsmVal.getMsgHeaderLen() + 2).toUpperCase();

            int CodeValue = ((int) hsmCode.charAt(0) - 65) * 100 + ((int) hsmCode.charAt(1) - 65);
            //In ASCII Table
            //'A' values 65
            //'B' values 66
            //'C' values 67
            //'D' values 68
            //'F' values 70
            //'1' values 49
            //'3' values 51

            resHsmVal.setHsmErrCode(hsmMsg.substring(resHsmVal.getMsgHeaderLen() + 2, resHsmVal.getMsgHeaderLen() + 4).toUpperCase());
            switch (CodeValue) {
                case 203: //(67-65)*100 +(68-65)=203 = 'CD'
                    resHsmVal.setMsgSecType(msgSecurityEnum.IN_NEED_OF_PIN);

                    if (resHsmVal.getHsmErrCode().equals("00")) {
                        resHsmVal.setHsmReturnValue(hsmMsg.substring(hsmMsg.length() - 18, hsmMsg.length() - 2));
                    }
                    resHsmVal.setCommandLen(resHsmVal.getMsgHeaderLen() + 2 + resHsmVal.getHsmErrCode().length() + 20);
                    if (hsmMsg.length() < resHsmVal.getCommandLen()) {
                        resHsmVal.setCommandLen(hsmMsg.length());
                    }

                    break;
                case 501: //(70-65)*100 +(66-65)=203 = 'FB'
                    resHsmVal.setMsgSecType(msgSecurityEnum.NET_ZPK_TRASLATE_ZMK_LMK);

                    if (resHsmVal.getHsmErrCode().equals("00")) {
                        resHsmVal.setHsmReturnValue(hsmMsg.substring(resHsmVal.getMsgHeaderLen() + 5, resHsmVal.getMsgHeaderLen() + 37));
                        resHsmVal.setKeyCheckValue(hsmMsg.substring(resHsmVal.getMsgHeaderLen() + 37, resHsmVal.getMsgHeaderLen() + 43));
                    }
                    resHsmVal.setCommandLen(resHsmVal.getMsgHeaderLen() + 2 + resHsmVal.getHsmErrCode().length() + 39);
                    if (hsmMsg.length() < resHsmVal.getCommandLen()) {
                        resHsmVal.setCommandLen(hsmMsg.length());
                    }

                    break;
                case 801: //(73-65)*100 + (66-65) = 801 = 'IB'
                    resHsmVal.setMsgSecType(msgSecurityEnum.NET_ZPK_GENERATE_ZMK);

                    if (resHsmVal.getHsmErrCode().equals("00")) {
                        resHsmVal.setHsmReturnValue(hsmMsg.substring(resHsmVal.getMsgHeaderLen() + 5, resHsmVal.getMsgHeaderLen() + 70));
                        resHsmVal.setKeyCheckValue(hsmMsg.substring(resHsmVal.getMsgHeaderLen() + 70, resHsmVal.getMsgHeaderLen() + 76));
                    }
                    resHsmVal.setCommandLen(resHsmVal.getMsgHeaderLen() + 2 + resHsmVal.getHsmErrCode().length() + 72);
                    if (hsmMsg.length() < resHsmVal.getCommandLen()) {
                        resHsmVal.setCommandLen(hsmMsg.length());
                    }

                    break;
                case 701:    // (72-65)*100 + (66-65) = 701 = 'HB'
                    resHsmVal.setMsgSecType(msgSecurityEnum.NET_TAK_GENERATE_ZMK);

                    if (resHsmVal.getHsmErrCode().equals("00")) {
                        resHsmVal.setHsmReturnValue(hsmMsg.substring(resHsmVal.getMsgHeaderLen() + 5, resHsmVal.getMsgHeaderLen() + 37));
                    }
                    resHsmVal.setCommandLen(resHsmVal.getMsgHeaderLen() + 2 + resHsmVal.getHsmErrCode().length() + 66);
                    if (hsmMsg.length() < resHsmVal.getCommandLen()) {
                        resHsmVal.setCommandLen(hsmMsg.length());
                    }

                    break;
                case -16:    // (65-65)*100 + (49-65) = -16 = 'A1'
                    resHsmVal.setMsgSecType(msgSecurityEnum.NET_TAK_GENERATE_ZMK);

                    if (resHsmVal.getHsmErrCode().equals("00")) {
                        resHsmVal.setHsmReturnValue(hsmMsg.substring(resHsmVal.getMsgHeaderLen() + 5, resHsmVal.getMsgHeaderLen() + 70));
                        resHsmVal.setKeyCheckValue(hsmMsg.substring(resHsmVal.getMsgHeaderLen() + 70, resHsmVal.getMsgHeaderLen() + 76));
                    }
                    resHsmVal.setCommandLen(resHsmVal.getMsgHeaderLen() + 2 + resHsmVal.getHsmErrCode().length() + 72);
                    if (hsmMsg.length() < resHsmVal.getCommandLen()) {
                        resHsmVal.setCommandLen(hsmMsg.length());
                    }

                    break;
                case 1207:    // (77-65)*100 + (72-65) = 1207 = 'MH'
                    resHsmVal.setMsgSecType(msgSecurityEnum.NET_TAK_TRANSLATE_LMK_ZMK);

                    if (resHsmVal.getHsmErrCode().equals("00")) {
                        resHsmVal.setHsmReturnValue(hsmMsg.substring(resHsmVal.getMsgHeaderLen() + 5, resHsmVal.getMsgHeaderLen() + 37));
                        resHsmVal.setKeyCheckValue(hsmMsg.substring(resHsmVal.getMsgHeaderLen() + 37, resHsmVal.getMsgHeaderLen() + 43));
                    }
                    resHsmVal.setCommandLen(resHsmVal.getMsgHeaderLen() + 2 + resHsmVal.getHsmErrCode().length() + 39);
                    if (hsmMsg.length() < resHsmVal.getCommandLen()) {
                        resHsmVal.setCommandLen(hsmMsg.length());
                    }

                    break;
                case 1209:    // (77-65)*100 + (74-65) = 1209 = 'MJ'
                    resHsmVal.setMsgSecType(msgSecurityEnum.NET_TAK_TRANSLATE_ZMK_LMK);

                    if (resHsmVal.getHsmErrCode().equals("00")) {
                        resHsmVal.setHsmReturnValue(hsmMsg.substring(resHsmVal.getMsgHeaderLen() + 5, resHsmVal.getMsgHeaderLen() + 37));
                        resHsmVal.setKeyCheckValue(hsmMsg.substring(resHsmVal.getMsgHeaderLen() + 37, resHsmVal.getMsgHeaderLen() + 43));
                    }
                    resHsmVal.setCommandLen(resHsmVal.getMsgHeaderLen() + 2 + resHsmVal.getHsmErrCode().length() + 39);
                    if (hsmMsg.length() < resHsmVal.getCommandLen()) {
                        resHsmVal.setCommandLen(hsmMsg.length());
                    }

                    break;
                case 1211:    // (77-65)*100 + (76-65) = 1211 = 'ML'
                    resHsmVal.setMsgSecType(msgSecurityEnum.IN_NEED_OF_MACGEN);

                    if (resHsmVal.getHsmErrCode().equals("00")) {
                        resHsmVal.setHsmReturnValue(hsmMsg.substring(resHsmVal.getMsgHeaderLen() + 4, resHsmVal.getMsgHeaderLen() + 12));
                    }
                    resHsmVal.setCommandLen(resHsmVal.getMsgHeaderLen() + 2 + resHsmVal.getHsmErrCode().length() + 8);
                    if (hsmMsg.length() < resHsmVal.getCommandLen()) {
                        resHsmVal.setCommandLen(hsmMsg.length());
                    }

                    break;
                case 1213:    // (77-65)*100 + (78-65) = 1213 = 'MN'
                    resHsmVal.setMsgSecType(msgSecurityEnum.IN_NEED_OF_MACVER);

                    resHsmVal.setCommandLen(resHsmVal.getMsgHeaderLen() + 2 + resHsmVal.getHsmErrCode().length());

                    break;
                /*                case 1184: // (77-65)*100 + (49-65) = 1184 'M1'
                 resHsmVal.setMsgSecType(msgSecurityEnum.ENCRYPT_DEK);
                 switch (CommonLib.valueOf(resHsmVal.getHsmErrCode())) {
                 case 0:
                 resHsmVal.setHsmReturnValue(hsmMsg.substring(resHsmVal.getMsgHeaderLen() + 8, resHsmVal.getMsgHeaderLen() + 8 + CommonLib.convertHexToInt(hsmMsg.substring(resHsmVal.getMsgHeaderLen() + 4, resHsmVal.getCommandLen() + 8))));
                 break;
                 default:

                 }
                 resHsmVal.setCommandLen(resHsmVal.getMsgHeaderLen() + 2 + resHsmVal.getHsmErrCode().length());
                 break;
                 case 1186: // (77-65)*100 + (51-65) = 1186 'M3'
                 resHsmVal.setMsgSecType(msgSecurityEnum.DECRYPT_DEK);
                 switch (CommonLib.valueOf(resHsmVal.getHsmErrCode())) {
                 case 0:
                 resHsmVal.setHsmReturnValue(hsmMsg.substring(resHsmVal.getMsgHeaderLen() + 8, resHsmVal.getMsgHeaderLen() + 8 + CommonLib.convertHexToInt(hsmMsg.substring(resHsmVal.getMsgHeaderLen() + 4, resHsmVal.getCommandLen() + 8))));
                 break;
                 default:

                 }
                 resHsmVal.setCommandLen(resHsmVal.getMsgHeaderLen() + 2 + resHsmVal.getHsmErrCode().length());
                 break;*/
                case 101:// BB response forming PinBlock

                    resHsmVal.setMsgSecType(msgSecurityEnum.IN_NEED_GEN_PIN);
                    switch (CommonLib.valueOf(resHsmVal.getHsmErrCode())) {
                        case 0:
                            resHsmVal.setHsmReturnValue(hsmMsg.substring(8));
                            break;
                        default:

                    }
                    resHsmVal.setCommandLen(resHsmVal.getMsgHeaderLen() + 2 + resHsmVal.getHsmErrCode().length() + 8);
                    if (hsmMsg.length() < resHsmVal.getCommandLen()) {
                        resHsmVal.setCommandLen(hsmMsg.length());
                    }

                    break;
                case 907: //JH forming PinBlock
                    resHsmVal.setMsgSecType(msgSecurityEnum.IN_NEED_GEN_PIN_ZPK);
                    switch (CommonLib.valueOf(resHsmVal.getHsmErrCode())) {
                        case 0:
                            resHsmVal.setHsmReturnValue(hsmMsg.substring(8));
                            break;
                        default:

                    }
                    resHsmVal.setCommandLen(resHsmVal.getMsgHeaderLen() + 2 + resHsmVal.getHsmErrCode().length() + 16);
                    if (hsmMsg.length() < resHsmVal.getCommandLen()) {
                        resHsmVal.setCommandLen(hsmMsg.length());
                    }
                    break;
                default:
                    resHsmVal.setMsgSecType(msgSecurityEnum.READY);
                    resHsmVal.setCommandLen(hsmMsg.length());

                    break;
            }
        } catch (Exception hsmErr) {
            CommonLib.PrintScreen(systemGlobal, "ShowHSMDataMessage HSM Parsing Error: " + hsmMsg,showLogEnum.DETAILMODE );
            resHsmVal.setCommandLen(hsmMsg.length());
        }
        return resHsmVal;
    }

    public void SendCommand(String pCommand) throws bicsexception {
        sendData(pCommand);
    }

    public threadTypeEnum getThreadType() {
        return threadTypeEnum.HSM;
    }

    public int getInstIndex() {
        return institutionIndex;
    }

    public boolean getSocketState() {
        return isRunning;
    }

    public void close() {
        try {
            incomingData.close();
            outgoingData.close();
        } catch (Exception ex) {
        }

        try {
            if (hsmClientSocket != null) {
                hsmClientSocket.close();
            }
        } catch (Exception ex) {
            CommonLib.PrintScreen(systemGlobal, "Err: Close connection to " + institutionIndex + ":" + ex.getMessage(), showLogEnum.DETAILMODE);
        }

    }
}
