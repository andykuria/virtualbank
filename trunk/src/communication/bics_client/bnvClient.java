/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package communication.bics_client;

import cfg.cfgNode;
import ginterface.iqueryactionable;

import ginterface.ithreadMonitor;
import ginterface.ithreadSequence;
import globalutils.LogActionEnum;

import globalutils.systemconfig;
import globalutils.threadTypeEnum;
import iso8583.ConfigIsoMessage;
import iso8583.HeaderProcessing;
import iso8583.IsoMessage;
import iss.showLogEnum;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.*;
import lib.CommonLib;
import processing.systemMessageQueue;
import unisim201401.systemLoader;

/**
 * @author minhdbh
 */
public class bnvClient extends Thread implements ithreadMonitor, ithreadSequence {

    private String server = "";
    private int port = 0;
    private String instituttionCode = "";
    private int instituttionIndex = -1;
    private String identifyState = "";
    private DataOutputStream outgoingData;
    private DataInputStream incomingData;
    private Socket bnvClientSocket = null;
    //private PrintWriter ServerWriter;
    private boolean isRunning = true;
    //--private boolean dirrectRunnung=true;
    byte[] messageStream = new byte[]{};
    HeaderProcessing msgheaderInfo;
    systemMessageQueue incommingqueue;
    private cfgNode isoCfg;

    private int localPort = 0;
    private iqueryactionable instData;
    private Thread parentThread = null;
    private boolean ismaster = true;

    private systemLoader systemGlobal;

    public void setSystemGlobal(systemLoader systemGlobal) {
        this.systemGlobal = systemGlobal;
    }

    public void setMasterInterface(boolean pmaster) {
        ismaster = pmaster;
    }

    public void setParentThread(Thread pThread) {
        this.parentThread = pThread;
    }
    private boolean threadStatus = false;

    public boolean getStatus() {
        return threadStatus;
    }

    public void setInstData(iqueryactionable pinstData) {
        this.instData = pinstData;
    }

    public void setMsgheaderInfo(HeaderProcessing pmsgheaderInfo) {
        this.msgheaderInfo = pmsgheaderInfo;
    }

    public void setIsoCfg(cfgNode pisoCfg) {
        this.isoCfg = pisoCfg;
    }

    public void setInstituttionIndex(int pinstituttionIndex) {
        this.instituttionIndex = pinstituttionIndex;
    }

    public bnvClient() {
        isRunning = false;
        localPort = 0;
        msgheaderInfo = new HeaderProcessing(4);
        setDaemon(false);
    }

    public bnvClient(String pinstitutionCode) {
        isRunning = false;
        msgheaderInfo = new HeaderProcessing(4);
        setInstituttionCode(pinstitutionCode);
        setDaemon(false);
    }

    public void setIncommingqueue(systemMessageQueue pincommingqueue) {
        this.incommingqueue = pincommingqueue;
    }

    public void setRunningMode(boolean isDirrect) {
        //--dirrectRunnung=isDirrect;
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
                            CommonLib.PrintScreen(systemGlobal, String.format("ERR SOCK - %s (%s): couldn't do waiting Connection", identifyState, port), showLogEnum.DEFAULT);

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
        CommonLib.PrintScreen(systemGlobal, String.format("SOCK - %s: (%s): connection is started", identifyState, port), showLogEnum.DEFAULT);

        try {
            while (isRunning) {
                byte[] data = new byte[1024];
                int datalen = -1;
                messageStream = new byte[]{};
                while (isRunning) {
                    if ((datalen = incomingData.read(data)) != -1) {
                        try {

                            byte[] truedata = new byte[datalen];
                            System.arraycopy(data, 0, truedata, 0, datalen);

                            String dataInHuman = CommonLib.getHumanFormatFromByte(truedata);

                            messageStream = CommonLib.concatByteArray(messageStream, truedata);
                            CommonLib.PrintScreen(systemGlobal, String.format("SOCKET.%s REV: %s \n\rBUFF 1(%d): %s", identifyState, dataInHuman, messageStream.length, CommonLib.getHumanFormatFromByte(messageStream)), showLogEnum.SIMPLEMODE);
                            byte[] msg = new byte[]{};
                            HeaderProcessing headerMsg = msgheaderInfo.clone();
                            if (headerMsg.initHeaderFromBytes(messageStream)) {
                                msg = headerMsg.getMessageFromBuffer(messageStream);
                                while (msg.length > 0) {
                                    IsoMessage isoMsg = new IsoMessage(msg, getInstituttionCode(), headerMsg, isoCfg, instData.getLineMode());
                                    if (isoMsg.isMessage()) {
                                        isoMsg.setSeqID(CommonLib.getNextSequence());
                                        isoMsg.setPortIndex(ismaster ? 0 : 1);
                                        isoMsg.setMsgType(CommonLib.getMsgType(isoMsg.getField(0)));

                                        CommonLib.PrintScreen(systemGlobal, identifyState + " parsed: " + isoMsg.printedMessage(), showLogEnum.DEFAULT);

                                        incommingqueue.systemmessagequeue(isoMsg);

                                        //Added by DUNGBTK 08.11.2010
                                        messageStream = CommonLib.copyByteArrayFromArray(messageStream, msg.length, messageStream.length - msg.length);
                                        CommonLib.PrintScreen(systemGlobal, String.format("SOCKET.%s BUFF 2(%d): %s", identifyState, messageStream.length, CommonLib.getHumanFormatFromByte(messageStream)), showLogEnum.SIMPLEMODE);
                                        //msg = CommonLib.getMsgFromBuffer(messageStream, msgheaderInfo);
                                        headerMsg = msgheaderInfo.clone();
                                        headerMsg.initHeaderFromBytes(messageStream);
                                        msg = headerMsg.getMessageFromBuffer(messageStream);

                                    } else {
                                        CommonLib.PrintScreen(systemGlobal, String.format("ERR SOCK - %s (%s) parsing (close connection): %s", identifyState, port, CommonLib.getHumanFormatFromByte(msg)),showLogEnum.DETAILMODE);
                                        isRunning = false;
                                        break;
                                    }
                                }
                                if (messageStream.length > 0) {
                                    CommonLib.PrintScreen(systemGlobal, String.format("SOCKET.%s BUFF 3(%d): %s", identifyState, messageStream.length, CommonLib.getHumanFormatFromByte(messageStream)), showLogEnum.DETAILMODE);
                                }
                            }
                        } catch (Exception e) {
                            messageStream = new byte[]{};
                            CommonLib.PrintScreen(systemGlobal, String.format("ERR SOCK - %s (%s) data error (close connection): %s", identifyState, port, e.getMessage()),showLogEnum.DETAILMODE);
                            isRunning = false;
                            break;
                        }
                    } else {
                        CommonLib.PrintScreen(systemGlobal, String.format("ERR SOCK - %s (%s) is interrupted..", identifyState, port), showLogEnum.DETAILMODE);
                        isRunning = false;
                        break;
                    }
                }
                //--Thread.sleep(GlobalObject.GW_SYS_Paras.getIntValue("SYSTEM",getSocketName() + "_SockRunningInterval"));
            }
            close();
        } catch (Exception ex) {
            isRunning = false;
            close();
            CommonLib.PrintScreen(systemGlobal, String.format("ERR SOCK - %s (%s): Connection error", identifyState, port),showLogEnum.DETAILMODE);
        }
    }

    private void initSocket() {
        try {
            if (localPort <= 0) {
                bnvClientSocket = new Socket(server, port);
            } else {
                bnvClientSocket = new Socket(server, port, InetAddress.getLocalHost(), localPort);
            }
            System.out.printf(identifyState + " INST connected to (%s,%d) at (%d)\n", server, port, bnvClientSocket.getLocalPort());
            incomingData = new DataInputStream(bnvClientSocket.getInputStream());
            outgoingData = new DataOutputStream(bnvClientSocket.getOutputStream());
            bnvClientSocket.setSoLinger(true, 0);
            isRunning = true;
            
            threadStatus = true;

        } catch (IOException ex) {
            isRunning = false;
            CommonLib.PrintScreen(systemGlobal, String.format("ERR SOCK - %s (%s): Connection error", identifyState, port), showLogEnum.DETAILMODE);
        } catch (Exception ex) {
            isRunning = false;
            CommonLib.PrintScreen(systemGlobal, String.format("ERR SOCK - %s (%s): Connection error", identifyState, port), showLogEnum.DETAILMODE);
        }
    }

    public void sendData(byte[] value) {
        try {

            outgoingData.write(value);
            //outgoingData.println("Echo Test, bnvClient sent");
            outgoingData.flush();
            CommonLib.PrintScreen(systemGlobal, String.format("SOCK %s (%s) SEND: %s", identifyState, port, CommonLib.asHex(value)), showLogEnum.DETAILMODE);
            /*
             switch (msgheaderInfo.getHeaderType()) {
             case ASCII:
             case SEQ_ITMX:
             CommonLib.PrintScreen(instituttionCode + " send: " + CommonLib.getHumanFormatFromByte(value));
             break;
             default:
             byte[] headerByte = new byte[msgheaderInfo.getHeaderLength()];
             System.arraycopy(value, 0, headerByte, 0, msgheaderInfo.getHeaderLength());
             byte[] asciiMessage = new byte[value.length - msgheaderInfo.getEtxLength() - msgheaderInfo.getHeaderLength() + 1];
             System.arraycopy(value, msgheaderInfo.getHeaderLength(), asciiMessage, 0, value.length - msgheaderInfo.getEtxLength() - msgheaderInfo.getHeaderLength() + 1);
             CommonLib.PrintScreen(instituttionCode + " send: (" + CommonLib.getIntStringOfArray(headerByte) + " ) " + new String(asciiMessage));
             }*/
        } catch (Exception ex) {
            isRunning = false;

            CommonLib.PrintScreen(systemGlobal, String.format("ERR SOCK %s: on %s:%s", identifyState, server, port), showLogEnum.DETAILMODE);

        }
    }

    public void sendData(String value) {
        sendData(value.getBytes());

    }

    /**
     * Set the IP of organization which GW is connected
     *
     * @param value: IP of org
     */
    public void setServer(String value) {
        server = value;
    }

    public void setLocalPort(int plocalPort) {
        this.localPort = plocalPort;
    }

    /**
     * Set port of server which is listening at the org's server
     *
     * @param value: port listener at server
     */
    public void setPort(int value) {
        port = value;
    }

    public boolean getSockState() {
        return isRunning;
    }

    public final void setInstituttionCode(String pinstituttionCode) {
        this.instituttionCode = pinstituttionCode;
        // identifyState = instituttionCode + "_Sock";
    }

    public String getInstituttionCode() {
        return instituttionCode;
    }

    public void setIdentifyState(String pidentifyState) {
        identifyState = pidentifyState;
        //identifyState = instituttionCode + "_Sock";
    }

    public threadTypeEnum getThreadType() {
        return threadTypeEnum.CLIENT_SOCK;
    }

    public int getInstIndex() {
        return instituttionIndex;
    }

    public String getInstName() {
        return instituttionCode;
    }

    public void close() {
        try {
            if (incomingData != null) {
                incomingData.close();
            }
            if (outgoingData != null) {
                outgoingData.close();
            }
        } catch (Exception ex) {
        }

        try {
            if (bnvClientSocket != null) {
                bnvClientSocket.close();

            }
        } catch (Exception ex) {
            CommonLib.PrintScreen(systemGlobal, "Err: Close connection to " + identifyState + ":" + ex.getMessage() + "\t Port: " + port, showLogEnum.DETAILMODE);
        }
    }
}
