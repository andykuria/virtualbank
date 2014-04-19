/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lib;

import globalutils.ConfigInfo;
import globalutils.KeyExhType;
import globalutils.LineModeEnum;
import globalutils.SystemState;
import iso8583.HeaderProcessing;
import iso8583.isolib;
import sun.io.ByteToCharCp500;
import globalutils.systemconfig;
import iso8583.IsoMessage;
import iso8583.IsoMessageType;
import iss.showLogEnum;
import java.security.MessageDigest;
import java.text.NumberFormat;
import java.util.List;
import java.util.Random;
import unisim201401.systemLoader;

/**
 *
 * @author Administrator
 */
public class CommonLib {

    public static Integer IsoMessageSequence;
    public static char[] hexCharTable = {
        '0', '1', '2', '3',
        '4', '5', '6', '7',
        '8', '9', 'A', 'B',
        'C', 'D', 'E', 'F'};
//AA00246F59
    private static final char[] HEX_CHARS = "0123456789abcdef".toCharArray();

    public static String asHex(byte[] buf) {
        char[] chars = new char[2 * buf.length];
        try {
            for (int i = 0; i < buf.length; ++i) {
                chars[2 * i] = HEX_CHARS[(buf[i] & 0xF0) >>> 4];
                chars[2 * i + 1] = HEX_CHARS[buf[i] & 0x0F];
            }
        } catch (Exception ex) {
            chars = "ERROR TO GET HEX".toCharArray();
        }

        return new String(chars);
    }

    public static byte[] hex2Byte(String strInHex) {
        byte[] bytes = new byte[strInHex.length() / 2];
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = (byte) Integer.parseInt(strInHex.substring(2 * i, 2 * i + 2), 16);
        }
        return bytes;
    }

    public static byte[] getMsgFromBuffer(byte[] buffer, HeaderProcessing pmsgHeader) {
        byte[] message = new byte[]{};
        byte[] msgHeader = isolib.getHeaderMessage(buffer, pmsgHeader);

        if (msgHeader.length > 0) {
            pmsgHeader.initHeaderFromBytes(msgHeader);
            int msgLength = pmsgHeader.getIsoMessageLengthValue();

            /*switch (pmsgHeader.getHeaderType())
             {
             case ASCII:
             msgLength=isolib.getMessageLength(msgHeader,pmsgHeader.getHeaderType());
             break;
             case SEQ_ITMX:
             msgLength=isolib.getMessageLength(msgHeader,pmsgHeader.getHeaderType())-12;
             break;
             case BYTE:
             msgLength=CommonLib.getIntFromByteArray(msgHeader);
             break;
             default:msgLength=isolib.getMessageLength(msgHeader,pmsgHeader.getHeaderType());
             }*/
            if (msgLength != 0) {
                if (buffer.length >= msgLength + msgHeader.length) {
                    message = copyByteArrayFromArray(buffer, 0, msgLength + msgHeader.length);
                }
            }
            /*if (!isolib.CheckMessageLength(message, pmsgHeader )){
             message = copyByteArrayFromArray(buffer, 0, 0);
             }*/
            //return message;
        }
        //return null;
        return message;
    }

    //Lay gia tri la mang 4 bytes cua MTI tu Socket doc duoc.
    //Dieu nay la rat can thiet cho viec xac dinh do la thong diep
    //yeu cau hay thong diep tra loi, loai thong diep la gi: thong diep
    //tai chinh, thong diep dao hay thong diep mang
    //Dau vao la mot mang cac byte nhan duoc tu Socket ket noi voi IST
    //mang byte nay bao gom ca MessageLength, MessageHeader, MTI + Bitmap + DE
    public static byte[] GetMTIFromMessage(byte[] bufferSocket) {
        byte[] MTI = new byte[4];
        System.arraycopy(bufferSocket, 4, MTI, 0, 4);
        return MTI;
    }

    /**
     * Ham dung de phan tich mot Primary Bitmap (dang Hexa) sang dang String gom
     * 0 va 1 tuong ung phuc vu cho viec kiem soat ve sau nay Tra lai "error"
     * neu dau vao ko phai la PrimaryBitmap
     *
     * @param PrimaryBitmap
     * @return
     */
    public static String getBinaryBitmapFromHexa(String bitmapValue) {
        String rt = "";
        int i;
        if (bitmapValue.length() != 16) {
            return "error";
        } else {
            for (i = 0; i <= 15; i++) {
                rt = rt + convertHexaToBinary(Character.toString(bitmapValue.charAt(i)));
            }
            return rt;
        }
    }

    /**
     * Ham dung de phan tich mot Binary String sang dang Hexa String dau vao gom
     * 4 ki tu 0 va 1 Dau ra la ki tu Hexa tuong ung
     *
     * @param binaryValue: hexa string in binary format
     * @return
     */
    public static String getHexaFromBinary(String binaryValue) {
        String rt = "";
        /*for(int i=0; i< binaryValue.length(); i++){
         char cHex=hexCharTable[Integer.parseInt(Character.toString(binaryValue.charAt(i)))*(int)(Math.pow(2, (binaryValue.length() - i - 1)))];
         rt = rt +Character.toString(cHex);
         }*/

        if (binaryValue.equals("0000")) {
            rt = "0";
        }
        if (binaryValue.equals("0001")) {
            rt = "1";
        }
        if (binaryValue.equals("0010")) {
            rt = "2";
        }
        if (binaryValue.equals("0011")) {
            rt = "3";
        }
        if (binaryValue.equals("0100")) {
            rt = "4";
        }
        if (binaryValue.equals("0101")) {
            rt = "5";
        }
        if (binaryValue.equals("0110")) {
            rt = "6";
        }
        if (binaryValue.equals("0111")) {
            rt = "7";
        }
        if (binaryValue.equals("1000")) {
            rt = "8";
        }
        if (binaryValue.equals("1001")) {
            rt = "9";
        }
        if (binaryValue.equals("1010")) {
            rt = "A";
        }
        if (binaryValue.equals("1011")) {
            rt = "B";
        }
        if (binaryValue.equals("1100")) {
            rt = "C";
        }
        if (binaryValue.equals("1101")) {
            rt = "D";
        }
        if (binaryValue.equals("1110")) {
            rt = "E";
        }
        if (binaryValue.equals("1111")) {
            rt = "F";
        }
        return rt;

    }

    /**
     * Ham nay convert tu ki tu hexa sang ki tu binary
     *
     * @param str ki tu hexa dau vao
     * @return
     */
    public static String convertHexaToBinary(String str) {
        if (str.equals("0")) {
            return "0000";
        }
        if (str.equals("1")) {
            return "0001";
        }
        if (str.equals("2")) {
            return "0010";
        }
        if (str.equals("3")) {
            return "0011";
        }
        if (str.equals("4")) {
            return "0100";
        }
        if (str.equals("5")) {
            return "0101";
        }
        if (str.equals("6")) {
            return "0110";
        }
        if (str.equals("7")) {
            return "0111";
        }
        if (str.equals("8")) {
            return "1000";
        }
        if (str.equals("9")) {
            return "1001";
        }
        if (str.equals("A")) {
            return "1010";
        }
        if (str.equals("B")) {
            return "1011";
        }
        if (str.equals("C")) {
            return "1100";
        }
        if (str.equals("D")) {
            return "1101";
        }
        if (str.equals("E")) {
            return "1110";
        }
        if (str.equals("F")) {
            return "1111";
        }
        return "error";
    }

    /**
     * Format an integer value to String with fixed length
     *
     * @param value: iso value
     * @param fixedlen: number of postion in return String
     * @return
     */
    public static String formatIntToString(int value, int fixedlen) {
        String strInt = "";
        strInt = String.format("%" + String.valueOf(fixedlen) + "d", value).replace(" ", "0");
        return strInt;

    }

    /**
     * Format an String value to String with fixed length
     *
     * @param value: iso value
     * @addedChar Filled if missing
     * @param fixedlen: number of postion in return String
     * @return
     */
    public static String formatToString(String value, char addedChar, int fixedlen) {
        String strInt = value;
        if (value.length() > fixedlen) {
            strInt = value.substring(value.length() - fixedlen, value.length());
        } else {
            for (int i = 0; i < fixedlen - value.length(); i++) {
                strInt = addedChar + strInt;
            }
        }
        return strInt;
    }

    public static String formatToRightString(String value, char addedChar, int fixedlen) {
        String strInt = value;
        if (value.length() > fixedlen) {
            strInt = value.substring(value.length() - fixedlen, value.length());
        } else {
            for (int i = 0; i < fixedlen - value.length(); i++) {
                strInt = strInt + addedChar;
            }
        }
        return strInt;
    }

    /*
     * Reverse string: exp 123456 to 654321
     */
    public static String ReverseString(String pString) {
        return new StringBuffer(pString).reverse().toString();
    }

    public static double formatIsoFormatToNumber(String pisovalue) {
        double isonumbervalue = Double.parseDouble(pisovalue);

        return (isonumbervalue * 1.0) / 100;
    }

    public synchronized static String getSystemTrace() {
        if (systemconfig.SystemTrace == 999999) {
            systemconfig.SystemTrace = 1;
        } else {
            systemconfig.SystemTrace++;
        }
        return formatIntToString(systemconfig.SystemTrace, 6);
    }

    public synchronized static String getRefNo() {
        if (systemconfig.de37 >= Integer.MAX_VALUE) {
            systemconfig.de37 = 1;
        } else {
            systemconfig.de37++;
        }
        return formatIntToString(systemconfig.de37, 12);
    }

    /**
     * Print to Sreen when system configuration is allowed
     *
     * @param Show_log_Const: System configuration
     * @param messagetoPrint: message to print
     */
    public static void PrintScreen(systemLoader globalCfg, String messagetoPrint, showLogEnum typeOfLog) {
        if (messagetoPrint != "") {
            System.out.println("[" + DateUtils.getCurrentDateTime() + "]" + " " + messagetoPrint);
            if (globalCfg != null) {
                if (globalCfg.getTaLogs() != null) {
                    switch (typeOfLog) {

                        case DEFAULT:
                            globalCfg.addLogs(messagetoPrint);
                            break;
                        case SIMPLEMODE:
                            if (globalCfg.getsParas().isIsShowSimpleLogs()) {
                                globalCfg.addLogs(messagetoPrint);
                            }
                            break;
                        case CONNECTIONMODE:
                            if (globalCfg.getsParas().isIsShowCnns()) {
                                globalCfg.addLogs(messagetoPrint);
                            }
                            break;
                        case DETAILMODE:
                            if (globalCfg.getsParas().isIsShowDetails()) {
                                globalCfg.addLogs(messagetoPrint);
                            }
                            break;
                    }
                }

            }
        }
    }

    /**
     * Check system ready state to process message when system is not ready
     * state, system is automatic response a message which RC=05
     *
     * @return true if system is ready to process message
     */
    public static boolean checkSystemState() {
        return systemconfig.SystemState.isEmpty();
    }

    public static void addSystemState(String key, String value) {
        if (!systemconfig.SystemState.containsKey(key)) {
            systemconfig.SystemState.put(key, value);
        }
    }

    public static void removeSystemState(String key) {
        if (systemconfig.SystemState.containsKey(key)) {
            systemconfig.SystemState.remove(key);
        }
    }

    public static int valueOf(String sNumber) {
        int num = 0;
        try {
            num = Integer.valueOf(sNumber.trim());
        } catch (Exception ex) {
        }
        return num;
    }

    public static String toHexString(byte[] input) {
        String resultValue = "";
        for (int i = 0; i < input.length; i++) {
            resultValue += toHexString(input[i]);
        }
        return resultValue;
    }

    public static String toHexString(byte input) {
        String sb = "";//new StringBuffer(  );
        sb += toHex(input >> 4);
        sb += toHex(input);
        return sb.toString();
    }

    private static char toHex(int nibble) {
        final char[] hexDigit = {
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'
        };
        return hexDigit[nibble & 0xF];
    }

    public static int getIntFromByteArray(byte[] input) {
        int resultValue = 0;
        int shiftLeft = (input.length - 1) * 8;
        for (int i = 0; i < input.length; i++) {
            resultValue |= (input[i] & 0xFF) << shiftLeft;
            if (shiftLeft > 0) {
                shiftLeft -= 8;
            }
        }
        return resultValue;
    }

    public static int getIntFromByte(byte input) {
        int resultValue = 0;
        resultValue |= (input & 0xFF);

        return resultValue;
    }

    public static byte getByteFromBinary(String binayInput) {
        byte rtValue = 0;
        try {
            int intValue = Integer.parseInt(formatToString(binayInput, '0', 8), 2);
            rtValue = CommonLib.convertIntToByte(intValue, 1)[0];
        } catch (Exception ex) {
        }
        return rtValue;
    }

    public static byte[] getByteArrayFromBinary(String binayInput) {
        int arrayLen = 0;
        int inputLen = binayInput.length();
        if (binayInput.length() % 8 == 0) {
            arrayLen = (int) binayInput.length() / 8;
        } else {
            arrayLen = (int) binayInput.length() / 8 + 1;
        }
        byte[] arrayResult = new byte[arrayLen];
        for (int i = arrayLen - 1; i >= 0; i--) {
            arrayResult[i] = getByteFromBinary(binayInput.substring(inputLen - (arrayLen - i) * 8, inputLen - (arrayLen - i - 1) * 8));
        }
        return arrayResult;
    }

    public static byte[] convertIntToByte(int value, int returnLen) {
        if (returnLen > 4) {
            returnLen = 4;
        }
        byte[] result = new byte[4];
        for (int i = 0; i < 4; i++) {
            int offset = (result.length - 1 - i) * 8;
            result[i] = (byte) ((value >>> offset) & 0xFF);
        }
        byte[] returnValue = new byte[returnLen];
        System.arraycopy(result, 4 - returnLen, returnValue, 0, returnLen);
        return returnValue;
    }

    public static String getIntStringOfArray(byte[] source) {
        String intString = "";
        for (int i = 0; i < source.length; i++) {
            intString += " " + String.valueOf((int) source[i]);
        }
        return intString;
    }

    public static byte[] concatByteArray(byte[] source, byte[] addedArray) {
        byte[] concatArray = new byte[source.length + addedArray.length];
        System.arraycopy(source, 0, concatArray, 0, source.length);
        System.arraycopy(addedArray, 0, concatArray, source.length, addedArray.length);
        return concatArray;
    }

    public static byte[] copyByteArrayFromArray(byte[] source, int start, int len) {
        byte[] arrayResult = new byte[]{};
        if (start < source.length - 1) {
            int remainLen = len + start > source.length ? source.length - start : len;
            arrayResult = new byte[remainLen];
            System.arraycopy(source, start, arrayResult, 0, remainLen);

        }
        return arrayResult;
    }

    public static byte[] copyByteArrayFromArray(byte[] source, int start) {
        byte[] arrayResult = new byte[]{};
        if (start < source.length - 1) {
            int remainLen = source.length - start;
            arrayResult = new byte[remainLen];
            System.arraycopy(source, start, arrayResult, 0, remainLen);

        }
        return arrayResult;
    }

    public static char[] convertEBCDIC_to_charASCII(byte[] inputEBCD) {
        char[] asciiResult = new char[]{};
        sun.io.ByteToCharCp500 cvt = new ByteToCharCp500();
        try {
            asciiResult = cvt.convertAll(inputEBCD);
        } catch (Exception ex) {
        }
        return asciiResult;
    }

    public static int convertHexToInt(String phex) {
        try {
            return Integer.parseInt(phex, 16);
        } catch (Exception ex) {
            return -1;
        }
    }

    public static String convertEBCDIC_to_StringASCII(byte[] inputEBCD) {
        return new String(convertEBCDIC_to_charASCII(inputEBCD));
    }

    public static byte[] convertASCII_to_ByteEBCDIC(String inputAscii) {
        byte[] ebcdResult = new byte[]{};
        try {

            ebcdResult = inputAscii.getBytes("Cp500");
        } catch (Exception ex) {
        }
        return ebcdResult;
    }

    public static String convertASCII_to_StringEBCDIC(String inputAscii) {
        return new String(convertASCII_to_ByteEBCDIC(inputAscii));
    }

    /**
     * Get new messageID by system, using this funtion when send command to HSM
     * It will help system know the source from which response
     *
     * @return New HSM ID
     */
    public synchronized static String getHSMCommandID() {
        if (systemconfig.HSMCommandID == 9999) {
            systemconfig.HSMCommandID = 1;
        } else {
            systemconfig.HSMCommandID++;
        }
        return formatIntToString(systemconfig.HSMCommandID, 4);
    }

    public static String getSystemStatedetail() {
        String rs = "";
        if (!systemconfig.SystemState.isEmpty()) {
            for (int i = 0; i < systemconfig.SystemState.size(); i++) {
                rs += "," + systemconfig.SystemState.keySet().toArray()[i].toString();
            }
        }
        return rs;
    }

    public static Object resizeArray(Object oldArray, int newSize) {
        int oldSize = java.lang.reflect.Array.getLength(oldArray);
        Class elementType = oldArray.getClass().getComponentType();
        Object newArray = java.lang.reflect.Array.newInstance(
                elementType, newSize);
        int preserveLength = Math.min(oldSize, newSize);
        if (preserveLength > 0) {
            System.arraycopy(oldArray, 0, newArray, 0, preserveLength);
        }
        return newArray;
    }

    //Xu ly F123 - MEPS
    public static String makeF123(int mti, int netCode, KeyExhType pDE53, ConfigInfo pCfg, String keyValue, String errCode) {
        String f123 = "";

        switch (mti) {
            case 800:
            case 820:
            case 821:
                switch (netCode) {
                    case 161:   //Change key Request
                        f123 = "CSM(MCL/RSI RCV/" + pCfg.getValue("INSTITUTION", "REV_INST_CODE") + " ORG/" + pCfg.getValue("INSTITUTION", "ORG_INST_CODE") + " SVR/)";
                        break;
                    case 162:   //New key Request
                        switch (pCfg.getIntValue("INSTITUTION", "TYPE")) {
                            case 2://MEPS
                            case 5: //ITMX
                            case 4://ITMX
                                String keycounterStr = "";
                                int keyCounterInt = 0;
                                switch (pDE53) {
                                    case TAK_INBOUND:
                                        keyCounterInt = CommonLib.convertHexToInt(pCfg.getValue("INSTITUTION", "CTP_TAK_OUT"));
                                        break;
                                    case TAK_OUTBOUND:
                                        keyCounterInt = CommonLib.convertHexToInt(pCfg.getValue("INSTITUTION", "CTP_TAK_IN"));
                                        break;
                                    case ZPK_INBOUND:
                                        keyCounterInt = CommonLib.convertHexToInt(pCfg.getValue("INSTITUTION", "CTP_ZPK_OUT"));
                                        break;
                                    case ZPK_OUTBOUND:
                                        keyCounterInt = CommonLib.convertHexToInt(pCfg.getValue("INSTITUTION", "CTP_ZPK_IN"));
                                        break;
                                }
                                keycounterStr = CommonLib.formatToString(Integer.toHexString(keyCounterInt + 1), '0', 14);
                                f123 = "CSM(MCL/KSM RCV/" + pCfg.getValue("INSTITUTION", "REV_INST_CODE") + " ORG/" + pCfg.getValue("INSTITUTION", "ORG_INST_CODE") + " KD/" + keyValue + String.format(" CTP/%s )", keycounterStr);
                                break;

                            //f123 = "CSM(MCL/KSM RCV/" + pCfg.getValue("INSTITUTION", "REV_INST_CODE") + " ORG/" + pCfg.getValue("INSTITUTION", "ORG_INST_CODE")  + " KD/" + keyValue + " CTP/00000000000633 )";
                            //break;
                            default:
                                f123 = "CSM(MCL/KSM RCV/" + pCfg.getValue("INSTITUTION", "REV_INST_CODE") + " ORG/" + pCfg.getValue("INSTITUTION", "ORG_INST_CODE") + " KD/" + keyValue + " CTP/)";
                        }

                        break;
                    default:
                        break;
                }
                break;
            case 810:
            case 830:
                if (errCode.equals("00")) {
                    f123 = "CSM(MCL/RSM RCV/" + pCfg.getValue("INSTITUTION", "REV_INST_CODE") + " ORG/" + pCfg.getValue("INSTITUTION", "ORG_INST_CODE") + " )";
                } else {
                    f123 = "CSM(MCL/ESM RCV/" + pCfg.getValue("INSTITUTION", "REV_INST_CODE") + " ORG/" + pCfg.getValue("INSTITUTION", "ORG_INST_CODE") + " ERF/" + errCode + " )";
                }
                break;
            default:
                break;
        }

        return f123;
    }

    public static String getKeyFromF123(String f123) {
        String key = "";
        //f123 = "CSM(MCL/KSM RCV/MEPS ORG/BNVN KD/456451231564 CTP/)";
        int idxOfKey = f123.indexOf(" KD/");
        int idxOfCTP = f123.indexOf(" CTP/");
        key = f123.substring(idxOfKey + 4, idxOfCTP);
        return key;
    }

    public static String getHumanFormatFromByte(byte[] pRevData) {
        String rs = "";
        try {
            for (int i = 0; i < pRevData.length; i++) {
                if (checkByteHuman(pRevData[i])) {
                    rs = rs + new String(new byte[]{pRevData[i]});
                } else {
                    rs = rs + "(" + String.valueOf((int) pRevData[i]) + ")";
                }
            }
        } catch (Exception ex) {
            rs = "ERROR TO GET HUMAN FORMAT";
        }

        return rs;
    }

    public static boolean checkByteHuman(byte pByte) {
        //true is ascii false is byte
        int byteInInt = (int) pByte;
        if ((byteInInt < 32) || (byteInInt > 126)) {
            return false;
        } else {
            return true;
        }
    }

    public static SystemState getSystemStateFromString(String pSystemIdentify) {
        SystemState pResult = SystemState.STATUS;
        if (pSystemIdentify.indexOf(String.valueOf(SystemState.DB)) >= 0) {
            return SystemState.DB;
        }
        if (pSystemIdentify.indexOf(String.valueOf(SystemState.DC_SOCKET)) >= 0) {
            return SystemState.DC_SOCKET;
        }
        if (pSystemIdentify.indexOf(String.valueOf(SystemState.DS_SOCKET)) >= 0) {
            return SystemState.DS_SOCKET;
        }
        if (pSystemIdentify.indexOf(String.valueOf(SystemState.HSM)) >= 0) {
            return SystemState.HSM;
        }
        if (pSystemIdentify.indexOf(String.valueOf(SystemState.KEY)) >= 0) {
            return SystemState.KEY;
        }
        if (pSystemIdentify.indexOf(String.valueOf(SystemState.SC4_SOCKET_0)) >= 0) {
            return SystemState.SC4_SOCKET_0;
        }
        if (pSystemIdentify.indexOf(String.valueOf(SystemState.SC4_SOCKET_1)) >= 0) {
            return SystemState.SC4_SOCKET_1;
        }
        if (pSystemIdentify.indexOf(String.valueOf(SystemState.SC_SOCKET)) >= 0) {
            return SystemState.SC_SOCKET;
        }
        if (pSystemIdentify.indexOf(String.valueOf(SystemState.SS4_SOCKET_0)) >= 0) {
            return SystemState.SS4_SOCKET_0;
        }
        if (pSystemIdentify.indexOf(String.valueOf(SystemState.SS4_SOCKET_1)) >= 0) {
            return SystemState.SS4_SOCKET_1;
        }
        if (pSystemIdentify.indexOf(String.valueOf(SystemState.SS_SOCKET)) >= 0) {
            return SystemState.SS_SOCKET;
        }
        if (pSystemIdentify.indexOf(String.valueOf(SystemState.STATUS)) >= 0) {
            return SystemState.STATUS;
        }

        return pResult;
    }

    public static LineModeEnum getLineMode(String pStrLineMode) {
        if (pStrLineMode.toUpperCase().equals("EBCDIC")) {
            return LineModeEnum.EBCDIC;
        }
        return LineModeEnum.ASCII;
    }

    public synchronized static Integer getNextSequence() {
        if (IsoMessageSequence == null) {
            IsoMessageSequence = 0;
        }
        if (IsoMessageSequence < Integer.MAX_VALUE) {
            IsoMessageSequence = IsoMessageSequence + 1;
        } else {
            IsoMessageSequence = 1;
        }
        return IsoMessageSequence;
    }

    public String getSettDate(String pCutOff) {
        return "";
    }

    public static String getHash(String pRawValue) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            messageDigest.update(pRawValue.getBytes());
            String encryptedString = new String(messageDigest.digest());
            return asHex(encryptedString.getBytes());
        } catch (Exception ex) {
            return "";
        }

    }

    public static IsoMessageType getMsgType(String mti) {
        switch (CommonLib.valueOf(mti)) {
            case 200:
            case 420:
            case 100:
                return IsoMessageType.REQUEST;
            case 210:
            case 110:
            case 430:
                return IsoMessageType.RESPONSE;
            case 800:
            case 820:
            case 620:
                return IsoMessageType.NETWORK_REQUEST;
            case 810:
            case 830:
            case 630:
                return IsoMessageType.NETWORK_RESPONSE;
        }
        return IsoMessageType.DROP;
    }

    public static int getStringCode(String pStrValue) {
        pStrValue = pStrValue.toUpperCase();
        int rs = 0;
        int multiratio = 1;
        for (int i = pStrValue.length() - 1; i >= 0; i--) {
            rs += (pStrValue.charAt(i) - 65) * multiratio;
            multiratio = multiratio * 100;

        }
        return rs;
    }

    public static String getAmmount(int minAmm, int maxAmm, int digitUnit, int lengthAmm) {
        Random rand = new Random();
        int amm = rand.nextInt(Math.abs(maxAmm - minAmm));
        return CommonLib.formatIntToString((minAmm + amm) * (int) Math.pow(10, digitUnit), lengthAmm);
    }

    public static String formatMessageListToString(List<IsoMessage> msgList, int typeOfStr) {
        //typeofstr: 1 is short form
        //2 is full form
        String rs = "";
        if (msgList != null) {
            if (msgList.size() > 0) {
                rs += "Process multi message. Total = " + msgList.size();
                for (IsoMessage imsg : msgList) {
                    switch (typeOfStr) {
                        case 1:
                            rs += "\n\r" + imsg.getTraceInfo();
                            break;
                        case 2:
                            rs += "\n\r--------------------------------\n\r" + imsg.printedMessage();
                        default:
                    }
                }
            }
        }

        return rs;

    }

}
