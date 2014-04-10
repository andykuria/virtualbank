/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package iso8583;

import cfg.cfgNode;
import exceptionshandle.bicsexception;
import ginterface.iInstitutionSecurity;
import ginterface.iiso8583;
import globalutils.LineModeEnum;

import java.util.Date;
import java.util.Iterator;
import java.util.Queue;

import java.util.concurrent.ConcurrentLinkedQueue;
import lib.CommonLib;
import lib.DateTimeEnum;
import lib.DateUtils;
import lib.msgSecurityEnum;
import lib.secObjInfo;

/**
 *
 * @author Administrator
 */
public class IsoMessage extends iso8583message implements iiso8583, Cloneable {
    
    private Integer seqID = 0;
    private Date receiveDatetime = new Date();
    private HeaderProcessing msgHeader;
    private String sourceInstitutionCode = "";
    private String desInstitutionCode = "";
    private IsoMessageType msgType = IsoMessageType.DEFAULT;
    
    private Queue<secObjInfo> secRequest = new ConcurrentLinkedQueue();
    
    private int portIndex = 0; //use for Duplex mode or some connections having more 1 socket
    private int delaytime = 0;
    
    public void setSeqID(Integer pseqID) {
        this.seqID = pseqID;
    }
    
    public Integer getSeqID() {
        return seqID;
    }
    
    public IsoMessage() {
        receiveDatetime = DateUtils.getDate();
        msgHeader = new HeaderProcessing(4);
        seqID = CommonLib.getNextSequence();
    }

    /**
     * Contructor, will parse iso message from byte[] input
     *
     * @param value: byte[] iso message value = Header + MTI + Bitmap +
     * DataElements[2->128]
     */
    public IsoMessage(byte[] value) {
        try {
            msgHeader = new HeaderProcessing(isolib.getHeaderMessage(value, new HeaderProcessing(4)));
            receiveDatetime = DateUtils.getDate();
            super.initMessage(isolib.removeHeaderMessage(value, msgHeader));
        } catch (bicsexception ex) {
            super.setMessageState(false);
        }
    }
    
    public IsoMessage(byte[] value, String pinstitutionCode, cfgNode pIsoCfg) {
        try {
            setIsoCfg(pIsoCfg);
            this.sourceInstitutionCode = pinstitutionCode;
            msgHeader = new HeaderProcessing(isolib.getHeaderMessage(value, new HeaderProcessing(4)));
            receiveDatetime = DateUtils.getDate();
            super.initMessage(isolib.removeHeaderMessage(value, msgHeader));
            
        } catch (bicsexception ex) {
            super.setMessageState(false);
        }
    }
    
    public IsoMessage(byte[] value, String pinstitutionCode, HeaderProcessing pmsgHeader, cfgNode pIsoCfg, LineModeEnum pMode) {
        try {
            setLineMode(pMode);
            setIsoCfg(pIsoCfg);
            setmsgHeader(pmsgHeader);
            this.sourceInstitutionCode = pinstitutionCode;
            
            receiveDatetime = DateUtils.getDate();
            super.initMessage(isolib.removeHeaderMessage(value, msgHeader));
            
        } catch (bicsexception ex) {
            String messageInHuman = CommonLib.getHumanFormatFromByte(value);
            
            super.setMessageState(false);
        }
    }

    //value = Header + MTI + Bitmap + DataElements[2->128]
    public IsoMessage(String value) {
        try {
            msgHeader = new HeaderProcessing(isolib.getHeaderMessage(value.getBytes(), new HeaderProcessing(4)));
            receiveDatetime = DateUtils.getDate();
            super.initMessage(isolib.removeHeaderMessage(value.getBytes(), msgHeader));
        } catch (bicsexception ex) {
            super.setMessageState(false);
        }
    }
    
    public IsoMessage(String value, String pinstitutionCode) {
        try {
            this.sourceInstitutionCode = pinstitutionCode;
            msgHeader = new HeaderProcessing(isolib.getHeaderMessage(value.getBytes(), new HeaderProcessing(4)));
            receiveDatetime = DateUtils.getDate();
            super.initMessage(isolib.removeHeaderMessage(value.getBytes(), msgHeader));
        } catch (bicsexception ex) {
            super.setMessageState(false);
            
        }
    }
    
    public IsoMessage(IsoMessage value) {
        super(value);
        receiveDatetime = value.getReceiveDatetime();
        //super.setMessageState(value.isMessage());

        sourceInstitutionCode = value.getSourceInterfaceCode();
        desInstitutionCode = value.getDesInterfaceCode();
        
        msgHeader = value.getMsgHeader().clone();
        secRequest = value.getSecurityRequestQueue();
        setPortIndex(value.getPortIndex());
        seqID = value.getSeqID();
        msgType = value.getMsgType();
        
    }
    
    public IsoMessageType getMsgType() {
        return msgType;
    }
    
    public void setMsgType(IsoMessageType msgType) {
        this.msgType = msgType;
    }
    
    public IsoMessage(String[] fields) {
        super(fields);
        receiveDatetime = DateUtils.getDate();
        //interfaceCode = "";
        //msgHeader = HeaderProcessing.makeHeaderMessage(fields.toString(), interfaceCode);
    }
    
    @Override
    public void setDate(Date value) {
        receiveDatetime = value;
    }
    
    @Override
    public Date getReceiveDatetime() {
        return receiveDatetime;
    }
    
    public void setmsgHeader(HeaderProcessing pmsgHeader) {
        msgHeader = pmsgHeader;
    }
    
    @Override
    public HeaderProcessing getMsgHeader() {
        return msgHeader;
    }

    /*@Override
     public String getInstitutionCode() {
     return sourceInstitutionCode;
     }
    
     @Override
     public void setInstitutionCode(String pinstitutionCode) {
     this.sourceInstitutionCode = pinstitutionCode;
     }*/
    @Override
    public String getSourceInterfaceCode() {
        return sourceInstitutionCode;
    }
    
    @Override
    public void setSourceInterfaceCode(String pSourceInterfaceCode) {
        sourceInstitutionCode = pSourceInterfaceCode;
    }
    
    @Override
    public String getDesInterfaceCode() {
        return desInstitutionCode;
    }
    
    @Override
    public void setDesInterfaceCode(String pDesInterfaceCode) {
        desInstitutionCode = pDesInterfaceCode;
    }
    
    public int getMsgCodeInInt() {
        switch (CommonLib.valueOf(getField(0))) {
            case 800:
            case 810:
            case 820:
            case 821:
            case 830:
                return 800;
            case 100:
            case 110:
            case 200:
            case 210:
            case 220:
            case 230:
                return 001;
            default:
                return 100;
        }
    }
    
    public String getTraceInfo() {
        String traceMsg = "";
        traceMsg = "{ID: {%s} SI: %s \t DI: %s \t MI: %s \t HT: %s}";
        return String.format(traceMsg, getSeqID(), getSourceInterfaceCode(), getDesInterfaceCode(), getMessageID(), String.valueOf(getMsgHeader().getHeaderType()));
    }
    
    public String getCurrencyInfo() {
        String currencyInfo = "";
        currencyInfo = "{ID: {%s} SI: %s \t DI: %s \t F4: %s \t F5: %s \t F6: %s \t F9: %s \t F10: %s}";
        return String.format(currencyInfo, getSeqID(), getSourceInterfaceCode(), getDesInterfaceCode(), getField(4), getField(5), getField(6), getField(9), getField(10));
    }
    
    @Override
    public String toString() {
        return toStringIsoMsg();
    }

    /**
     * Convert IsoMessage to String
     *
     * @return iso message string
     */
    private String toStringIsoMsg() {
        return new String(toByte());
    }
    
    public String getIsoHeaderString() {
        switch (msgHeader.getHeaderType()) {
            case ASCII:
                return "";
            case SEQ_ITMX:
                return msgHeader.genITMX_Header(getField(0), "");
            default:
                return "";
        }
    }
    
    public String getBitmapForMacing(Integer pdirection) {
        String fullBitmap = new String(genarateBitmapForMAC(this.getIsoFields(), pdirection));
        switch (msgHeader.getHeaderType()) {
            case ASCII:
                return fullBitmap;
            case SEQ_ITMX:
                return fullBitmap.substring(0, 16);
            default:
                return fullBitmap;
        }
        
    }

    /**
     * Convert Iso Message to byte[]
     *
     * @return byte array of iso message
     */
    public byte[] toByte() {

        //return toStringIsoMsg().getBytes();
        byte[] resultBytes;
        byte[] isoByte = super.toBytes();
        switch (CommonLib.valueOf(getField(70))) {
            case 999://Ideal message
                resultBytes = new byte[4];
                System.arraycopy("0000".getBytes(), 0, resultBytes, 0, 4);
                break;
            default:
                switch (msgHeader.getHeaderType()) {
                    case ASCII:
                        resultBytes = new byte[isoByte.length + msgHeader.getFullHeaderLength()];
                        System.arraycopy(CommonLib.formatIntToString(isoByte.length, msgHeader.getHeaderLength()).getBytes(), 0, resultBytes, 0, msgHeader.getFullHeaderLength());
                        System.arraycopy(isoByte, 0, resultBytes, msgHeader.getFullHeaderLength(), isoByte.length);
                        
                        break;
                    case SEQ_ITMX:
                        resultBytes = new byte[isoByte.length + msgHeader.getFullHeaderLength()];
                        System.arraycopy(CommonLib.convertIntToByte(isoByte.length + 12, 2), 0, resultBytes, 0, 2);
                        System.arraycopy(msgHeader.genITMX_Header(getField(0), "").getBytes(), 0, resultBytes, 2, 12);
                        System.arraycopy(isoByte, 0, resultBytes, msgHeader.getFullHeaderLength(), isoByte.length);
                        
                        break;
                    
                    case BYTE:
                        resultBytes = new byte[isoByte.length + msgHeader.getFullHeaderLength()];
                        System.arraycopy(CommonLib.convertIntToByte(isoByte.length + msgHeader.getEtxLength(), 2), 0, resultBytes, 0, 2);
                        System.arraycopy(isoByte, 0, resultBytes, msgHeader.getFullHeaderLength() - msgHeader.getEtxLength(), isoByte.length);
                        System.arraycopy(msgHeader.getEtx(), 0, resultBytes, isoByte.length + msgHeader.getFullHeaderLength() - msgHeader.getEtxLength(), msgHeader.getEtx().length);
                        
                        break;
                    case BINARY:
                    default:
                        resultBytes = new byte[isoByte.length + msgHeader.getFullHeaderLength()];
                        
                        System.arraycopy(CommonLib.formatIntToString(resultBytes.length - msgHeader.getHeaderLength(), msgHeader.getHeaderLength()).getBytes(), 0, resultBytes, 0, msgHeader.getHeaderLength());
                        System.arraycopy(msgHeader.genHeader(getField(0), CommonLib.formatIntToString(resultBytes.length - msgHeader.getHeaderLength(), msgHeader.getHeaderLength())), 0, resultBytes, msgHeader.getHeaderLength(), msgHeader.getExtraLength());
                        System.arraycopy(isoByte, 0, resultBytes, msgHeader.getFullHeaderLength(), isoByte.length);
                        break;
                }
        }
        
        return resultBytes;
    }
    
    public void addSecRequest(secObjInfo psecreq) {
        secRequest.add(psecreq);
    }
    
    public void setSecRequest(Queue<secObjInfo> psecQueueReq) {
        secRequest = psecQueueReq;
    }
    
    public void setSecRequest(iInstitutionSecurity imsgSec) {
        if (imsgSec != null) {
            setSecRequest(imsgSec.getSecurityList(this));
        }
    }
    
    public Queue<secObjInfo> getSecurityRequestQueue() {
        return secRequest;
    }
    
    @Override
    public synchronized secObjInfo peekSecRequest() {
        //if (secRequire.isSecurityDone())  
        if (secRequest == null) {
            return null;
        }
        try {
            return secRequest.poll();
        } catch (Exception ex) {
            return null;
        }

        //return null;
    }
    
    public synchronized secObjInfo getSecRequest() {
        //if (secRequire.isSecurityDone())  
        if (secRequest == null) {
            return null;
        }
        try {
            return secRequest.peek();
        } catch (Exception ex) {
            return null;
        }

        //return null;
    }
    
    public void clearAllSecurity() {
        secRequest.clear();
    }
    
    public String getSecurityInfo() {
        return String.format("SEC Request ID {%s} (%s)", getSeqID(), getRequestSec());
    }
    
    private String getRequestSec() {
        
        String rs = "";
        try {
            Iterator itr = secRequest.iterator();
            while (itr.hasNext()) {
                msgSecurityEnum iValue = (msgSecurityEnum) itr.next();
                rs += " " + String.valueOf(iValue);
            }
            
        } catch (Exception ex) {
        }
        return rs;
    }
    
    @Override
    public IsoMessage clone() {
        try {
            return (IsoMessage) super.clone();
        } catch (CloneNotSupportedException ce) {
            return null;
        }
    }
    
    public int getPortIndex() {
        return portIndex;
    }
    
    public void setPortIndex(int pPortIndex) {
        portIndex = pPortIndex;
    }
    
    public msg8583Type getType() {
        switch (CommonLib.valueOf(getField(0))) {
            case 200:
                switch (CommonLib.valueOf(getField(3).substring(0, 2))) {
                    case 1:
                        return msg8583Type.CW;
                    case 30:
                    default:
                        return msg8583Type.BAL;
                }
            
            case 420:
                return msg8583Type.REVERSAL;
            case 800:
                switch (CommonLib.valueOf(getField(70))) {
                    default:
                    case 301:
                        return msg8583Type.ECHO;
                    case 001:
                        return msg8583Type.SIGNON;
                    case 002:
                        return msg8583Type.SIGNOFF;
                    case 101:
                        return msg8583Type.NEWKEY;
                }
            
        }
        return msg8583Type.UNKNOWN;
    }
    
    public int getDelaytime() {
        return delaytime;
    }
    
    public void setDelaytime(int delaytime) {
        this.delaytime = delaytime;
    }
    
    public String getHashString() {
        return getField(2) + getField(3) + getField(4) + getField(5) + getField(11) + getField(13) + getField(41);
    }
    
    public int getHashCode() {
        return getHashString().hashCode();
    }
    
}
