/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package iso8583;

import globalutils.HeaderPartsEnum;
import lib.CommonLib;

/**
 *
 * @author Administrator
 */
public class HeaderProcessing implements Cloneable {

    private int vheaderLength = 0;
    private int vheaderExtraLength = 0;
    private IsoHeaderType vheaderType;
    private byte[] etx = new byte[0]; //DataEnd of Message
    private byte[] vheaderMsg;
    BICS_Data[] headerParts;
    private boolean isBNVStandard = true;

    public HeaderProcessing() {
        vheaderLength = 4;
        vheaderType = IsoHeaderType.ASCII;
        vheaderMsg = new byte[]{0, 0, 0, 0};
        headerParts = new BICS_Data[0];
        initHeaderParts();
    }

    public HeaderProcessing(int pHeaderlength) {
        vheaderLength = pHeaderlength;
        vheaderType = IsoHeaderType.ASCII;
        headerParts = new BICS_Data[0];
        initHeaderParts();
    }

    public HeaderProcessing(byte[] pheaderMsg) {
        vheaderLength = pheaderMsg.length;
        vheaderType = IsoHeaderType.ASCII;
        vheaderMsg = new byte[vheaderLength];
        System.arraycopy(pheaderMsg, 0, vheaderMsg, 0, vheaderLength);
        headerParts = new BICS_Data[0];
        initHeaderParts();

    }

    public HeaderProcessing(String pheaderMsg) {
        vheaderLength = pheaderMsg.length();
        vheaderType = IsoHeaderType.ASCII;
        vheaderMsg = new byte[vheaderLength];
        System.arraycopy(pheaderMsg.getBytes(), 0, vheaderMsg, 0, vheaderLength);
        headerParts = new BICS_Data[0];
        initHeaderParts();
    }

    public byte[] getEtx() {
        return etx;
    }

    public void InitEtx(String petxStr) {
        //04052012
        if (petxStr.equals("")) {
            etx = null;
        } //
        else {
            String[] etxStr = petxStr.split(" ");
            etx = new byte[etxStr.length];
            for (int i = 0; i < etx.length; i++) {
                etx[i] = Byte.valueOf(etxStr[i]);
            }
        }
    }

    public int getEtxLength() {

        return etx == null ? 0 : etx.length; // 04052012
    }

    public int getExtraLength() {
        /*int extraLength=0;
        for(int i=0;i<headerParts.length;i++) extraLength+=headerParts[i].getLength();*/
        return vheaderExtraLength;
    }

    public int getHeaderLength() {
        return vheaderLength;
    }

    public byte[] getHeaderMsg() {
        return vheaderMsg;
    }

    public IsoHeaderType getHeaderType() {
        return vheaderType;
    }

    public void setHeaderLength(int pheaderLength) {
        this.vheaderLength = pheaderLength;
    }

    public boolean initHeaderFromBytes(byte[] pBuffers) {
        boolean rs = false;
        if (pBuffers.length >= getFullHeaderLength()) {
            rs = true;
            vheaderMsg = new byte[getFullHeaderLength() - getEtxLength()];
            System.arraycopy(pBuffers, 0, vheaderMsg, 0, getFullHeaderLength() - getEtxLength());
            switch (vheaderType) {
                case SEQ_ITMX:
                    initITMX_Data();
                    break;
                case CUP:
                    initCUP_Data();
                    break;
            }
        } else {
            rs = false;
        }

        return rs;
    }

    public byte[] getMessageFromBuffer(byte[] pBuffers) {
        byte[] rs = new byte[0];
        if (pBuffers.length >= getIsoMessageLengthValue() + getHeaderLength()) {
            rs = new byte[getIsoMessageLengthValue() + getHeaderLength()];
            System.arraycopy(pBuffers, 0, rs, 0, getIsoMessageLengthValue() + getHeaderLength());
        }
        return rs;
    }

    private void initITMX_Header() {
        byte[] header_extra_data = "ISO016000000".getBytes();

        //headerParts[0].setData(vheaderMsg,0,2);
        headerParts[0].setData(header_extra_data, 0, 3);
        headerParts[1].setData(header_extra_data, 3, 2);
        headerParts[2].setData(header_extra_data, 5, 2);
        headerParts[3].setData(header_extra_data, 7, 3);
        headerParts[4].setData(header_extra_data, 10, 1);
        headerParts[5].setData(header_extra_data, 11, 1);
    }

    private void initITMX_Data() {
        byte[] header_extra_data = toBytes(HeaderPartsEnum.HEADER_EXTRA_DATA);

        //headerParts[0].setData(vheaderMsg,0,2);
        headerParts[0].setData(header_extra_data, 0, 3);
        headerParts[1].setData(header_extra_data, 3, 2);
        headerParts[2].setData(header_extra_data, 5, 2);
        headerParts[3].setData(header_extra_data, 7, 3);
        headerParts[4].setData(header_extra_data, 10, 1);
        headerParts[5].setData(header_extra_data, 11, 1);
    }

    private void initCUP_Data() {
        byte[] header_extra_data = toBytes(HeaderPartsEnum.HEADER_EXTRA_DATA);

        //headerParts[0].setData(vheaderMsg,0,2);
        headerParts[0].setData(header_extra_data, 0, 1);
        headerParts[1].setData(header_extra_data, 1, 1);
        headerParts[2].setData(header_extra_data, 2, 4);
        headerParts[3].setData(header_extra_data, 6, 11);
        headerParts[4].setData(header_extra_data, 17, 11);
        headerParts[5].setData(header_extra_data, 28, 3);
        headerParts[6].setData(header_extra_data, 31, 1);
        headerParts[7].setData(header_extra_data, 32, 8);
        headerParts[8].setData(header_extra_data, 40, 1);
        headerParts[9].setData(header_extra_data, 41, 5);
    }

    public void setHeaderType(IsoHeaderType pheaderType) {
        this.vheaderType = pheaderType;
        switch (pheaderType) {

            case ASCII:
                vheaderExtraLength = 0;
                headerParts = new BICS_Data[0];
                initHeaderParts();

                break;
            case BYTE:
                vheaderExtraLength = 0;
                headerParts = new BICS_Data[0];
                initHeaderParts();


                break;
            case SEQ_ITMX:
                isBNVStandard = false;
                vheaderExtraLength = 12;
                headerParts = new BICS_Data[6];
                initHeaderParts();
                //headerParts[0].setType(BICS_Data_Type.BNUMBER); //iso message length
                headerParts[0].setType(BICS_Data_Type.BANS); //ISO
                headerParts[1].setType(BICS_Data_Type.BANS_NUMBER);
                headerParts[2].setType(BICS_Data_Type.BANS_NUMBER);
                headerParts[3].setType(BICS_Data_Type.BANS_NUMBER);
                headerParts[4].setType(BICS_Data_Type.BANS_NUMBER);
                headerParts[5].setType(BICS_Data_Type.BANS_NUMBER);
                break;
            case CUP:
                //vheaderMsg=
                isBNVStandard = false;
                vheaderExtraLength = 46;
                headerParts = new BICS_Data[10];
                initHeaderParts();

                headerParts[0].setType(BICS_Data_Type.BNUMBER); //ISO
                headerParts[1].setType(BICS_Data_Type.BNUMBER);
                headerParts[2].setType(BICS_Data_Type.BANS);
                headerParts[3].setType(BICS_Data_Type.BANS);
                headerParts[4].setType(BICS_Data_Type.BANS);
                headerParts[5].setType(BICS_Data_Type.BBYTES);
                headerParts[6].setType(BICS_Data_Type.BNUMBER);
                headerParts[7].setType(BICS_Data_Type.BANS);
                headerParts[8].setType(BICS_Data_Type.BBYTES);
                headerParts[9].setType(BICS_Data_Type.BANS);

                break;
        }
    }

    private void initHeaderParts() {
        try {
            if (headerParts != null) {
                for (int i = 0; i < headerParts.length; i++) {
                    headerParts[i] = new BICS_Data();
                }
            }
        } catch (Exception ex) {
        }
    }

    public byte[] headerMessage() {
        return vheaderMsg;
    }

    public String toString() {
        return new String(vheaderMsg);
    }

    public String toString(HeaderPartsEnum pParts) {
        return new String(toBytes(pParts));
    }

    public byte[] toBytes(HeaderPartsEnum pParts) {
        byte[] headerPart = new byte[vheaderLength];
        switch (pParts) {
            case HEADER_EXTRA_DATA:
                headerPart = getHeaderExtraData();
                break;
            case HEADER_LENGTH:
                headerPart = getMsgLengthBytes();
                break;
        }
        return headerPart;
    }

    private byte[] getHeaderExtraData() {
        byte[] headerPart = new byte[0];
        switch (getHeaderType()) {
            case ASCII:
            case BYTE:
                break;
            case SEQ_ITMX:
                headerPart = new byte[12];
                System.arraycopy(vheaderMsg, 2, headerPart, 0, 12);
                break;
            case CUP:
                headerPart = new byte[46];
                System.arraycopy(vheaderMsg, 4, headerPart, 0, 46);
                break;
        }
        return headerPart;
    }

    private byte[] getMsgLengthBytes() {
        byte[] headerPart = new byte[0];
        switch (getHeaderType()) {
            case ASCII:

            //headerPart = new byte[4];
            //System.arraycopy(vheaderMsg, 0, headerPart, 0, 2);
            //break;
            case BYTE:
                headerPart = vheaderMsg;
                break;
            case SEQ_ITMX:
                headerPart = new byte[2];
                System.arraycopy(vheaderMsg, 0, headerPart, 0, 2);
                break;
            case CUP:
                headerPart = new byte[4];
                System.arraycopy(vheaderMsg, 0, headerPart, 0, 4);
                break;

        }
        return headerPart;
    }

    public int getIsoMessageLengthValue() {
        //String vheadervaluestr=toString();
        try {
            switch (getHeaderType()) {
                case CUP:
                case ASCII:
//                    return Integer.parseInt(vheadervaluestr);
                    return CommonLib.valueOf(toString(HeaderPartsEnum.HEADER_LENGTH));

                case BYTE:
                    return CommonLib.getIntFromByteArray(toBytes(HeaderPartsEnum.HEADER_LENGTH));

                case SEQ_ITMX:
                    //byte[] vheaderValue = new byte[2];
                    //System.arraycopy(vheaderMsg, 0, vheaderValue, 0, 2);
                    return CommonLib.getIntFromByteArray(toBytes(HeaderPartsEnum.HEADER_LENGTH));
                default:
                    return 0;

            }

        } catch (Exception ex) {
            return 0;
        }
    }

    @Override
    public HeaderProcessing clone() {
        try {
            return (HeaderProcessing) super.clone();
        } catch (CloneNotSupportedException ce) {
            return null;
        }


    }

    public byte[] genHeader(String pMTI, String isoLength) {
        byte[] headerInByte = new byte[0];
        switch (getHeaderType()) {
            case CUP:
                //headerInByte=new byte[46];
                headerInByte = CommonLib.concatByteArray(headerInByte, new byte[]{46});
                //headerInByte = CommonLib.concatByteArray(headerInByte, new byte[]{-127});
                headerInByte = CommonLib.concatByteArray(headerInByte, new byte[]{2});
                
                headerInByte = CommonLib.concatByteArray(headerInByte, isoLength.getBytes());
                headerInByte = CommonLib.concatByteArray(headerInByte, "00010344   26540704   ".getBytes());
                headerInByte = CommonLib.concatByteArray(headerInByte, new byte[]{0, 0, 0, 0});
                headerInByte = CommonLib.concatByteArray(headerInByte, "00000000".getBytes());
                headerInByte = CommonLib.concatByteArray(headerInByte, new byte[]{0});
                headerInByte = CommonLib.concatByteArray(headerInByte, "00000".getBytes());
                break;
            case SEQ_ITMX:
                String isoHeader = "ISO016000000";
                if (vheaderMsg == null) {
                    switch (CommonLib.valueOf(pMTI)) {
                        case 800:
                        case 810:
                            isoHeader = "ISO006000011";
                            break;
                        case 200:
                        case 210:
                        case 420:
                        case 421:
                        case 430:

                            isoHeader = "ISO016000076";

                            break;
                        default:
                            isoHeader = "ISO016000000";
                    }
                } else {
                    isoHeader = new String(getHeaderExtraData());
                }
                headerInByte = isoHeader.getBytes();

                break;
            default:
        }
        return headerInByte;
    }

    public String genITMX_Header(String pMTI, String isoLength) {

        /*String isoHeader = "ISO016000000";
        if (vheaderMsg == null) {
        switch (CommonLib.valueOf(pMTI)) {
        case 800:
        case 810:
        isoHeader = "ISO006000011";
        break;
        case 200:
        case 210:
        case 420:
        case 421:
        case 430:
        
        isoHeader = "ISO016000076";
        
        break;
        default:
        isoHeader = "ISO016000000";
        }
        } else {
        return new String(getHeaderExtraData());
        }
        
        return isoHeader;*/
        return new String(genHeader(pMTI, isoLength));

    }

    public int getFullHeaderLength() {
        return getHeaderLength() + getExtraLength() + getEtxLength();
    }

    public BICS_Data getHeaderPart(int iIndex) {
        if (headerParts != null) {
            if (headerParts.length > 0) {
                if (iIndex>=headerParts.length)
                {
                    return null;
                }
                else
                {
                    return headerParts[iIndex];
                }
            }
            else
            {
                return null;
            }
        }
        else
        {
            return null;
        }
    }
}
