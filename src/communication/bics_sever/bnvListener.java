/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package communication.bics_sever;

/**
 * @author minhdbh Use this object as a part of server having function of
 * "waiting data" from Socket Client
 */
import cfg.cfgNode;
import ginterface.iqueryactionable;

import globalutils.LineModeEnum;
import globalutils.LogActionEnum;
import iso8583.ConfigIsoMessage;
import iso8583.HeaderProcessing;
import iso8583.IsoMessage;
import iss.showLogEnum;
import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import lib.CommonLib;
import processing.systemMessageQueue;
import unisim201401.systemLoader;

public class bnvListener extends Thread {

    private ClientInfo mClientInfo;
    private DataInputStream mIn;
    private ServerDispatcher mServerDispatcher;
    byte[] messageStream = new byte[]{};
    //private String institutionCode="";
    private cfgNode isoCfg;
    private boolean isRunning = true;
    private String instituttionCode = "";
    private int instituttionIndex = -1;
    private String identifyState = "";
    HeaderProcessing msgheaderInfo;
    systemMessageQueue incommingqueue;
    private iqueryactionable instData;
    private Thread parentThread = null;
    private LineModeEnum dataLineMode;
    
     private systemLoader systemGlobal;

    public void setSystemGlobal(systemLoader systemGlobal) {
        this.systemGlobal = systemGlobal;
    }

    public void setLineMode(LineModeEnum pMode) {
        dataLineMode = pMode;
    }

    public void close() {
        isRunning = false;
        try {
            mIn.close();
        } catch (Exception ex) {
        }
        try {
            mClientInfo.close();
        } catch (Exception ex) {
        }
    }

    public bnvListener(ClientInfo aClientInfo, ServerDispatcher aServerDispatcher) throws IOException {
        mClientInfo = aClientInfo;
        Socket socket = aClientInfo.mSocket;
        mServerDispatcher = aServerDispatcher;
        mIn = new DataInputStream(socket.getInputStream());
    }

    private String getCharSet(Charset cs, String input) {
        String rs = "";
        ByteBuffer newRsCharset = cs.encode(input);
        rs = newRsCharset.asCharBuffer().toString();
        return rs;
    }

    /**
     * Until interrupted, reads messages from the client socket, forwards them
     * to the server dispatcher's queue and notifies the server dispatcher.
     */
    @Override
    @SuppressWarnings("SuspiciousSystemArraycopy")
    public void run() {
        //while (!isInterrupted()) {
        try {

            //String message="";
            byte[] data = new byte[1024];
            int datalen = -1;
            messageStream = new byte[]{};
            while (isRunning) {
                if ((datalen = mIn.read(data)) != -1) {
                    try {

                        byte[] truedata = new byte[datalen];
                        System.arraycopy(data, 0, truedata, 0, datalen);
                        if ((datalen == 4) && new String(truedata).equals("0000")) {
                            CommonLib.PrintScreen(systemGlobal, "SOCK - " + identifyState + " received IDLE: 0000",showLogEnum.DETAILMODE);
                        } else {
                            messageStream = CommonLib.concatByteArray(messageStream, truedata);
                            CommonLib.PrintScreen(systemGlobal, String.format("SOCKET.%s REV: %s \n\rBUFF 1(%d): %s", identifyState, CommonLib.getHumanFormatFromByte(truedata), messageStream.length, CommonLib.getHumanFormatFromByte(messageStream)),showLogEnum.SIMPLEMODE);
                            byte[] msg = new byte[]{};

                            HeaderProcessing headerMsg = msgheaderInfo.clone();
                            if (headerMsg.initHeaderFromBytes(messageStream)) {

                                msg = headerMsg.getMessageFromBuffer(messageStream);
                                while (msg.length > 0) {
                                    boolean needParsing = false;
                                    if (headerMsg.getHeaderPart(9) != null) {
                                        if (headerMsg.getHeaderPart(9).getValue().toString().equals("00000")) {
                                            needParsing = true;
                                        }
                                    } else {
                                        needParsing = true;
                                    }
                                    if (needParsing) {
                                        IsoMessage isoMsg = new IsoMessage(msg, getInstituttionCode(), headerMsg, isoCfg, instData.getLineMode());
                                        if (isoMsg.isMessage()) {
                                            isoMsg.setSeqID(CommonLib.getNextSequence());
                                            isoMsg.setMsgType(CommonLib.getMsgType(isoMsg.getField(0)));
                                            incommingqueue.systemmessagequeue(isoMsg);
                                            CommonLib.PrintScreen(systemGlobal, identifyState + " parsed: " + isoMsg.printedMessage(), showLogEnum.DEFAULT);

                                            //Added by DUNGBTK 08.11.2010
                                            messageStream = CommonLib.copyByteArrayFromArray(messageStream, msg.length, messageStream.length - msg.length);
                                            CommonLib.PrintScreen(systemGlobal, String.format("SOCKET.%s BUFF 2(%d): %s", identifyState, messageStream.length, CommonLib.getHumanFormatFromByte(messageStream)), showLogEnum.SIMPLEMODE);
                                            //msg = CommonLib.getMsgFromBuffer(messageStream, msgheaderInfo);
                                            headerMsg = msgheaderInfo.clone();
                                            headerMsg.initHeaderFromBytes(messageStream);
                                            msg = headerMsg.getMessageFromBuffer(messageStream);
                                        } else {
                                            CommonLib.PrintScreen(systemGlobal, "ERR SOCK - " + identifyState + " parsing (close connection): " + CommonLib.asHex(msg), showLogEnum.SIMPLEMODE);
                                            isRunning = false;
                                            mServerDispatcher.deleteClient(mClientInfo);
                                            break;
                                        }
                                    } else {
                                        CommonLib.PrintScreen(systemGlobal, "DROP BY HEADER - " + identifyState + CommonLib.asHex(msg), showLogEnum.DETAILMODE);
                                        messageStream = CommonLib.copyByteArrayFromArray(messageStream, msg.length, messageStream.length - msg.length);
                                        CommonLib.PrintScreen(systemGlobal, String.format("SOCKET.%s BUFF 2(%d): %s", identifyState, messageStream.length, CommonLib.getHumanFormatFromByte(messageStream)), showLogEnum.DETAILMODE);

                                        headerMsg = msgheaderInfo.clone();
                                        headerMsg.initHeaderFromBytes(messageStream);
                                        msg = headerMsg.getMessageFromBuffer(messageStream);
                                    }

                                }
                                if (messageStream.length > 0) {
                                    CommonLib.PrintScreen(systemGlobal, String.format("SOCKET.%s BUFF 3(%d): %s", identifyState, messageStream.length, CommonLib.getHumanFormatFromByte(messageStream)),showLogEnum.DETAILMODE);
                                }
                            }
                        }

                    } catch (Exception e) {
                        messageStream = new byte[]{};
                        CommonLib.PrintScreen(systemGlobal, "ERR SOCK " + identifyState + " data error (close connection): " + e.getMessage(),showLogEnum.DETAILMODE);

                        isRunning = false;
                        mServerDispatcher.deleteClient(mClientInfo);
                        break;
                    }
                } else {
                    CommonLib.PrintScreen(systemGlobal, "ERR SOCK " + identifyState + " is interrupted..",showLogEnum.DETAILMODE);
                    isRunning = false;
                    mServerDispatcher.deleteClient(mClientInfo);
                    break;
                }
            }
            //--Thread.sleep(GlobalObject.SystemParas.getIntValue("VNBC_SOCK_RUNNING_INTEVAL"));

        } catch (Exception ex) {
            isRunning = false;
            CommonLib.PrintScreen(systemGlobal, "ERR SOCK " + identifyState + ": Connection error",showLogEnum.DETAILMODE);
        }
        //Communication is broken. Interrupt both listener and sender threads

    }

    public void setIsoCfg(cfgNode pisoCfg) {
        isoCfg = pisoCfg;
    }

    public void setMsgheaderInfo(HeaderProcessing pmsgheaderInfo) {
        msgheaderInfo = pmsgheaderInfo;
    }

    public void setInstituttionIndex(int pinstituttionIndex) {
        instituttionIndex = pinstituttionIndex;
    }

    public int getInstituttionIndex() {
        return instituttionIndex;
    }

    public String getInstituttionCode() {
        return instituttionCode;
    }

    public void setInstituttionCode(String pinstituttionCode) {
        this.instituttionCode = pinstituttionCode;
    }

    public void setIdentifyState(String pidentifyState) {
        this.identifyState = pidentifyState;
    }

    public void setIncommingqueue(systemMessageQueue pincommingqueue) {
        this.incommingqueue = pincommingqueue;
    }

    public void setInstData(iqueryactionable pinstData) {
        this.instData = pinstData;
    }

    public void setParentThread(Thread pThread) {
        this.parentThread = pThread;
    }

    public void setDataLineMode(LineModeEnum pdataLineMode) {
        this.dataLineMode = pdataLineMode;
    }
}