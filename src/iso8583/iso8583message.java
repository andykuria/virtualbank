/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package iso8583;

import cfg.cfgNode;

import exceptionshandle.bicsexception;
import globalutils.LineModeEnum;
import lib.CommonLib;

/**
 *
 * @author minhdbh
 */
public abstract class iso8583message extends bicsexception implements Cloneable {

    private String[] strIsoFields = new String[129];    //MTI + Bitmap + DataElements[2->128]
    private boolean isIsoMessage = false;
    cfgNode isoCfg;
    private LineModeEnum dataLineMode;

    public void setLineMode(LineModeEnum pMode) {
        dataLineMode = pMode;
    }

    public void setIsoCfg(cfgNode pcfg) {
        this.isoCfg = pcfg;
    }

    public cfgNode getIsoCfg() {
        return this.isoCfg;
    }

    public iso8583message() {
        for (int i = 0; i < strIsoFields.length; i++) {
            strIsoFields[i] = "";
        }

        isIsoMessage = false;
        

    }

    /**
     * Contructor, will parse iso message from byte[] input
     * @param value: byte[] iso message
     * value = Header + MTI + Bitmap + DataElements[2->128]
     */
    public iso8583message(byte[] value) throws bicsexception {
        try {
            ContructMsgFromByte(value);
 
        } catch (bicsexception ex) {
            isIsoMessage = false;
            throw new bicsexception(ex.getErrMessage());
        }

    }

    //value = Header + MTI + Bitmap + DataElements[2->128]
    public iso8583message(String value) throws bicsexception {
        try {
            ContructMsgFromByte(value.getBytes());
 

        } catch (bicsexception ex) {
            throw new bicsexception(ex.getErrMessage());
        }
    }

    public iso8583message(iso8583message value) {
        this.isoCfg = value.getIsoCfg();
        for (int i = 0; i < strIsoFields.length; i++) {
            this.setField(i, value.getField(i));
        }
        this.setField(1, "");
        isIsoMessage = value.isMessage();

    }

    public iso8583message(String[] fields) {
        if (fields.length != strIsoFields.length) {
            //CommonLib.PrintScreen("ShowIsoMessageParsingState", "Error on contruction of IsoMessage");
            //GlobalObject.system_action_log.addData("Error on contruction of IsoMessage..");
            isIsoMessage = false;
        } else {
            for (int i = 0; i < fields.length; i++) {
                strIsoFields[i] = fields[i];
            }
            isIsoMessage = true;

        }
    }

    public void initMessage(byte[] value) throws bicsexception {
        try {
            ContructMsgFromByte(value);

        } catch (bicsexception ex) {
            isIsoMessage = false;
            throw new bicsexception(ex.getErrMessage());
        }
    }

    /**
     * Convert IsoMessage to String
     * @return iso message string
     */
    //value = Header + MTI + Bitmap + DataElements[2->128]
    private void ContructMsgFromByte(byte[] value) throws bicsexception {
        for (int i = 0; i < this.strIsoFields.length; i++) {
            this.strIsoFields[i] = "";
        }
        //Kiem tra do dai thong diep xem co dung ko va lay gia tri cua MTI
        try {
            switch (dataLineMode) {
                case ASCII:
                    this.strIsoFields = iso8583Utils.ParseIsoMessage(value, isoCfg);
                    break;
                case EBCDIC:
                    this.strIsoFields = iso8583Utils.ParseIsoMessageEBCDIC(value, isoCfg);
                    break;
            }

            isIsoMessage = (boolean) (this.strIsoFields.length == 129);
        } catch (bicsexception ex) {
            isIsoMessage = false;
            throw new bicsexception(ex.getErrMessage());
        }
    }

    public byte[] genarateBitmap(String[] value) {
        //Tinh Bitmap dua tren cac thanh phan da co
        String strBinaryBitmap = "";
        boolean haveSecondaryBitmap = false;
        String bitmap = "";
        for (int i = 2; i < value.length; i++) {
            if (!value[i].equals("")) {
                strBinaryBitmap = strBinaryBitmap + "1";
            } //strMessageCUP = strMessageCUP + strfieldsCUP[i];
            else {
                strBinaryBitmap = strBinaryBitmap + "0";
            }
        }

        //Kiem tra xem co thanh phan Secondary Bitmap ko?
        for (int i = 65; i < value.length; i++) {
            if (!value[i].equals("")) {
                haveSecondaryBitmap = true;
                break;
            }
        }

        //Bo sung vao chuoi strBinaryBitmapCUP de dam bao co hoac ko co SecondaryBitmap
        if (haveSecondaryBitmap == true) {
            strBinaryBitmap = "1" + strBinaryBitmap;
        } else {
            strBinaryBitmap = "0" + strBinaryBitmap;
        }

        //Doi chuoi thanh dang cua IST
        //cu xau gom 8 ky tu 0 va 1 se doi thanh 2 ky tu Hexa
        //tuc 4 ky tu 0 va 1 thanh 1 ky tu Hexa
        //Tinh luon BITMAP cua thong diep gui nguoc toi IST
        bitmap = "";  //Day chinh la thanh phan Bitmap


        if (isoCfg.checkBinaryField(2)) {
            bitmap = new String(CommonLib.getByteArrayFromBinary(strBinaryBitmap));
        } else {
            if (haveSecondaryBitmap == true) {
                //Co Secondary Bitmap, nen phai chay chuoi toi 128/4 = 32
                for (int i = 0; i < 32; i++) {
                    bitmap = bitmap + CommonLib.getHexaFromBinary(strBinaryBitmap.substring(i * 4, i * 4 + 4));
                }
            } else {
                //Khong co Secondary Bitmap, nen chay chuoi toi 64/4 = 16
                for (int i = 0; i < 16; i++) {
                    bitmap = bitmap + CommonLib.getHexaFromBinary(strBinaryBitmap.substring(i * 4, i * 4 + 4));
                }
            }
        }

        if (dataLineMode != null) {
            switch (dataLineMode) {
                case ASCII:

                    return bitmap.getBytes();//CommonLib.getByteArrayFromBinary(haveSecondaryBitmap==true?strBinaryBitmap:strBinaryBitmap.substring(0, 64));


                case EBCDIC:
                    return CommonLib.getByteArrayFromBinary(haveSecondaryBitmap == true ? strBinaryBitmap : strBinaryBitmap.substring(0, 64));
                default:
                    bitmap.getBytes();


            }
        } else {
            return bitmap.getBytes();
        }
        return bitmap.getBytes();
    }

    public byte[] genarateBitmapForMAC(String[] value, Integer pdirection) {
        //pdirection=0: request
        //pdirection=1: response
        //Tinh Bitmap dua tren cac thanh phan da co
        String strBinaryBitmap = "";
        boolean haveSecondaryBitmap = false;
        String bitmap = "";
        for (int i = 2; i < value.length; i++) {
            if (!value[i].equals("")) {
                strBinaryBitmap = strBinaryBitmap + "1";
            } //strMessageCUP = strMessageCUP + strfieldsCUP[i];
            else {
                strBinaryBitmap = strBinaryBitmap + "0";
            }
        }

        //Kiem tra xem co thanh phan Secondary Bitmap ko?
        for (int i = 65; i < value.length; i++) {
            if (!value[i].equals("")) {
                haveSecondaryBitmap = true;
                break;
            }
        }


        //Bo sung vao chuoi strBinaryBitmapCUP de dam bao co hoac ko co SecondaryBitmap
        if (haveSecondaryBitmap == true) {
            strBinaryBitmap = "1" + strBinaryBitmap;
            strBinaryBitmap = strBinaryBitmap.substring(0, strBinaryBitmap.length() - 1) + "1";
        } else {
            switch (pdirection) {
                case 0:
                    strBinaryBitmap = "1" + strBinaryBitmap;
                    break;
                case 1:
                    strBinaryBitmap = "0" + strBinaryBitmap;
                    break;

            }


            strBinaryBitmap = strBinaryBitmap.substring(0, 63) + "1" + strBinaryBitmap.substring(64, strBinaryBitmap.length());
        }

        //Doi chuoi thanh dang cua IST
        //cu xau gom 8 ky tu 0 va 1 se doi thanh 2 ky tu Hexa
        //tuc 4 ky tu 0 va 1 thanh 1 ky tu Hexa
        //Tinh luon BITMAP cua thong diep gui nguoc toi IST
        bitmap = "";  //Day chinh la thanh phan Bitmap
        if (haveSecondaryBitmap == true) {
            //Co Secondary Bitmap, nen phai chay chuoi toi 128/4 = 32
            for (int i = 0; i < 32; i++) {
                bitmap = bitmap + CommonLib.getHexaFromBinary(strBinaryBitmap.substring(i * 4, i * 4 + 4));
            }
        } else {
            //Khong co Secondary Bitmap, nen chay chuoi toi 64/4 = 16
            for (int i = 0; i < 16; i++) {
                bitmap = bitmap + CommonLib.getHexaFromBinary(strBinaryBitmap.substring(i * 4, i * 4 + 4));
            }
        }

        return bitmap.getBytes();//CommonLib.getByteArrayFromBinary(haveSecondaryBitmap==true?strBinaryBitmap:strBinaryBitmap.substring(0, 64));

    }

    private String getAsciiMessage() {
        strIsoFields[1] = CommonLib.toHexString(genarateBitmap(strIsoFields));
        String asciiMsg = "";
        for (int i = 0; i < 128; i++) {
            asciiMsg += strIsoFields[i];
        }
        return asciiMsg;

    }

    public String printedMessage() {
        //strIsoFields[1]=CommonLib.toHexString(genarateBitmap(strIsoFields));
        setField(1, new String(genarateBitmap(this.getIsoFields())));
        String fullMessagePrint = "Iso Message: ";
        //fullMessagePrint+="\n\tFULL message: "+getAsciiMessage()+"\n";
        for (int i = 0; i < strIsoFields.length; i++) {
            if (strIsoFields[i].equals("") == false) {
                fullMessagePrint += "\n\t" + "F" + String.valueOf(i) + ": " + CommonLib.getHumanFormatFromByte(getField(i).getBytes());
            }
        }
        if (isMessage()) {


            return fullMessagePrint;
        } else {
            return "Iso parsing error: " + "\n" + fullMessagePrint;
        }
    }

    public String getMessageID() {
        String messageID = "";
        switch (Integer.valueOf(strIsoFields[0])) {
            case 800:
            case 810:
            case 820:
            case 830:
                messageID = strIsoFields[0] + "|" + strIsoFields[7] + "|" + strIsoFields[11] + "|" + strIsoFields[32] + "|" + strIsoFields[70];
                break;
            case 200:
            case 210:
            case 420:
            case 421:
            case 430:
            default:
                messageID = strIsoFields[0] + "|" + strIsoFields[2] + "|" + strIsoFields[7] + "|" + strIsoFields[11] + "|" + strIsoFields[32];
                break;
        }
        return messageID;
    }

    public String getStrIsoFields() {
        String StrIsoFields = "";
        for (int i = 0; i < this.strIsoFields.length; i++) {
            if (this.getField(i).equals("") == false) {
                StrIsoFields = StrIsoFields + this.getField(i);
            }
        }
        return StrIsoFields;
    }

    public final void setField(int index, String value) {
        if (value.equals("")) {
            if ((index > 0) && (index < 129)) {
                strIsoFields[index] = "";
            }
        } else {
            if ((index >= 0) && (index < 129)) {
                strIsoFields[index] = isolib.createIsoValue(index, isoCfg, value);
            }
        }
    }

    public String getField(int index) {
        if ((index >= 0) && (index < 129)) {
            return isolib.getIsoValue(index, isoCfg, strIsoFields[index]);
        } else {
            return "";
        }
    }

    public void remove(int index) {
        setField(index, "");
    }

    public String[] getIsoFields() {
        return strIsoFields;
    }

    public int length() {
        return strIsoFields.length;
    }

    /**
     * Return true if this object contain well-known iso message
     * @return
     */
    public boolean isMessage() {
        return isIsoMessage;
    }

    public void setMessageState(boolean isIsoMessage) {
        this.isIsoMessage = isIsoMessage;
    }

    public String[] toArray() {
        String[] isoValueArray = new String[129];
        for (int i = 0; i < 129; i++) {
            isoValueArray[i] = getField(i);
        }
        return isoValueArray;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public String getInstitutionCode() {
        return "---";
    }

    public void setInstitutionCode(String pinstitutionCode) {
    }


    public int getMTI() {
        return CommonLib.valueOf(getField(0));

    }

    public byte[] toBytes() {
        switch (dataLineMode) {
            case ASCII:

                return toByteASCII();
            case EBCDIC:
                return toByteEBCDIC();

        }
        return new byte[]{};
    }
    
    public String getKeyPatternStr()
    {
         String rs= getField(2)+getField(11)+getField(12)+getField(13);
         rs += getField(41).equals("")?"        ":getField(41).substring(0, 8);
         return rs;
    }

    private byte[] toByteEBCDIC() {

        byte[] isoMsg = new byte[]{};
        setField(1, new String(genarateBitmap(this.getIsoFields())));
        //strIsoFields[1]=new String(genarateBitmap(strIsoFields));
        for (int i = 0; i < strIsoFields.length; i++) {
            if ((strIsoFields[i].equals("") == false) || (i == 1)) {
                if (i != 1) {
                    isoMsg = CommonLib.concatByteArray(isoMsg, CommonLib.convertASCII_to_ByteEBCDIC(strIsoFields[i]));
                } else {
                    isoMsg = CommonLib.concatByteArray(isoMsg, genarateBitmap(strIsoFields));
                }
                //isoMsg = isoMsg + strIsoFields[i];
            }
        }
        //isoMsg = CommonLib.concatByteArray(CommonLib.convertIntToByte(isoMsg.length,2),isoMsg) ;
        return isoMsg;
        //return toStringIST().getBytes();
    }

    private byte[] toByteASCII() {

        byte[] isoMsg = new byte[]{};
        setField(1, new String(genarateBitmap(this.getIsoFields())));
        //strIsoFields[1]=new String(genarateBitmap(strIsoFields));
        for (int i = 0; i < strIsoFields.length; i++) {
            if ((strIsoFields[i].equals("") == false) || (i == 1)) {
                if (i != 1) {
                    if (isoCfg.checkBinaryField(i)) {

                        isoMsg = CommonLib.concatByteArray(isoMsg, CommonLib.hex2Byte(strIsoFields[i]));


                    } else {
                        isoMsg = CommonLib.concatByteArray(isoMsg, strIsoFields[i].getBytes());
                    }


                } else {
                    if (isoCfg.checkBinaryField(i)) {
                        isoMsg = CommonLib.concatByteArray(isoMsg, CommonLib.hex2Byte(new String(genarateBitmap(strIsoFields))));
                    } else {
                        isoMsg = CommonLib.concatByteArray(isoMsg, genarateBitmap(strIsoFields));
                    }
                }
                //isoMsg = isoMsg + strIsoFields[i];
            }
        }
        //isoMsg = CommonLib.concatByteArray(CommonLib.convertIntToByte(isoMsg.length,2),isoMsg) ;
        return isoMsg;
        //return toStringIST().getBytes();
    }

   
}
