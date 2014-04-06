/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package iso8583;

import cfg.cfgNode;
import globalutils.ConfigInfo;
import lib.CommonLib;
import lib.DateTimeEnum;
import lib.DateUtils;

/**
 *
 * @author minhdbh
 */
public class isolib {

    /**
     * get iso Filed Value from message object without Length infomation
     * @param index
     * @return
     */
    public static String getIsoValue(int index, cfgNode pisocfg, String value) {
        String isoValue = "";
        if (!value.equals("")) {
            switch (pisocfg.getIntValue(index+"")) {
                case 99:
                    isoValue = value.substring(2, value.length());
                    break;
                case 999:
                    isoValue = isoValue = value.substring(3, value.length());
                    break;
                default:
                    isoValue = value;
                    break;
            }
        }
        return isoValue;
    }

    /**
     *get the value of Iso field, depends on the Field length and Field Type in Cofig
     * @param index: index of iso field in Iso Message
     * @param value: Value of Iso Field index
     * @return
     */
    public static String createIsoValue(int index, cfgNode pisoCfg, String value) {
        String isoValue = "";
        if (value != null) {
            if (!value.equals("")) {
                switch (pisoCfg.getIntValue(index+"")) {
                    case 99:
                        isoValue = CommonLib.formatIntToString(value.length(), 2) + value;
                        break;
                    case 999:
                        isoValue = CommonLib.formatIntToString(value.length(), 3) + value;
                        break;
                    default:
                        isoValue = value;
                        break;
                }
            }
        }
        return isoValue;

    }

    /**
     * Detail of Iso Field
     * @param value
     * @return
     */
    public static String getMsgDetail(IsoMessage value) {
        String rs = "";
        for (int i = 1; i < 129; i++) {
            if (!value.getField(i).equals("")) {
                rs += "\nField " + String.valueOf(i) + ": " + value.getField(i);
            }
        }
        return rs;
    }

    public static HeaderProcessing makeISTHeaderMessage(String strIsoFields) {
        int headerLength = 4;
        HeaderProcessing headerMessage = new HeaderProcessing(CommonLib.formatIntToString(strIsoFields.length(), headerLength));
        return headerMessage;
    }

    /*public static int getMessageLength(byte[] messageHeader, IsoHeaderType pHeaderType)
    {
    int msgLen = 0;
    try {
    HeaderProcessing msgHeader = new HeaderProcessing();
    msgHeader.setHeaderType(pHeaderType);
    msgHeader.setHeaderMsg(messageHeader);
    msgLen=msgHeader.getIsoMessageLengthValue();
    
    } catch (Exception ex) {
    msgLen = 0;
    }
    
    return msgLen;
    }*/
    public static byte[] removeHeaderMessage(byte[] message, HeaderProcessing intHeader) {
        //removed when ITMX connect
        //byte[] isoMessage = new byte[message.length - intHeader.getHeaderLength() -intHeader.getEtxLength() ];
        byte[] isoMessage = new byte[message.length - intHeader.getFullHeaderLength()];
        //System.arraycopy(message, intHeader.getHeaderLength(), isoMessage, 0, message.length- intHeader.getHeaderLength() -intHeader.getEtxLength() );
        System.arraycopy(message, intHeader.getFullHeaderLength() - intHeader.getEtxLength(), isoMessage, 0, message.length - intHeader.getFullHeaderLength());
        return isoMessage;
    }

    public static byte[] getHeaderMessage(byte[] message, HeaderProcessing intHeader) {

        //byte[] headerMsg = new byte[intHeader.getHeaderLength()];
        byte[] headerMsg = new byte[intHeader.getFullHeaderLength()-intHeader.getEtxLength()];
        if (message.length > intHeader.getFullHeaderLength()) {
            System.arraycopy(message, 0, headerMsg, 0, intHeader.getHeaderLength()-intHeader.getEtxLength());
            return headerMsg;
        }
        return new byte[]{};
    }

    public static String addHeaderMessage(String strIsoFields, HeaderProcessing intHeader) {
        return (intHeader.toString() + strIsoFields);
    }

    public static void initIsoMessageArray(IsoMessage[] pMsgArr, int pSize) {
        pMsgArr = new IsoMessage[pSize];
        for (int i = 0; i < pSize; i++) {
            pMsgArr[i] = new IsoMessage();
        }

    }

    ;
    
    public static boolean isDropState(IsoMessage pmsg, int ptimeout) {
        switch (CommonLib.valueOf(pmsg.getField(0))) {
            case 200:
            case 210:
                if (DateUtils.DateDiff(DateTimeEnum.SECOND, pmsg.getReceiveDatetime(), DateUtils.getDate()) >= ptimeout) {
                    return true;
                } else {
                    return false;
                }


            default:
                return false;

        }

    }

    public static String getIsoAscii(int pIndex, cfgNode pisocfg, byte[] rawIso) {
        String asciiResult = "";
        switch (pisocfg.getIntValue("TYPE")) {
            case 7://CUP
                switch (pIndex) {
                    case 48: //Key of CUP
                        asciiResult = new String(CommonLib.copyByteArrayFromArray(rawIso, 0, 2)) + CommonLib.toHexString(CommonLib.copyByteArrayFromArray(rawIso, 2, rawIso.length - 2));
                        break;
                    default:
                        asciiResult = pisocfg.checkBinaryField(pIndex) ? CommonLib.toHexString(CommonLib.copyByteArrayFromArray(rawIso, 0)) : new String(rawIso);
                }
                break;
            default:
                asciiResult = pisocfg.checkBinaryField(pIndex) ? CommonLib.toHexString(CommonLib.copyByteArrayFromArray(rawIso, 0)) : new String(rawIso);
        }


        return asciiResult;

    }

}
