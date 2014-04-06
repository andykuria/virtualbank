/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hsm;

import globalutils.HsmCommandEnum;
import lib.CommonLib;

/**
 *
 * @author minhdbh
 */
public class hsmLib {

    public static String TranslatePIN(String msgID, String pinblk, String acc, String acq_zpk, String iss_zpk) {
        String maxpinlen = "12";
        String spformat = "01"; //source pin block format
        String dpformat = "01"; //destination pin block format
        String cmd = CommonLib.formatToString(msgID, '0', 4) + "CCU" + acq_zpk + "U" + iss_zpk + maxpinlen + pinblk + spformat + dpformat + acc;
        return cmd;
    }

    public static String TranslateZPK_ZMK2ZPK_LMK(String msgID, String zmk_lmk, String zpk_zmk) {
        String cmd = CommonLib.formatToString(msgID, '0', 4) + "FAU" + zmk_lmk.toUpperCase() + "X" + zpk_zmk.toUpperCase() + ";0U1";
        return cmd;
    }

    public static String GenerateZPK(String msgID, String zmk_lmk) {
        String cmd = CommonLib.formatToString(msgID, '0', 4) + "IAU" + zmk_lmk.toUpperCase() + ";XU1";
        return cmd;
    }

    public static String GenerateTAK_HAcmd(String msgID, String tmk_lmk) {
        String cmd = CommonLib.formatToString(msgID, '0', 4) + "HAU" + tmk_lmk.toUpperCase() + ";XU0";
        return cmd;
    }

    public static String GenerateTAK(String msgID, String zmk_lmk) {
        String cmd = CommonLib.formatToString(msgID, '0', 4) + "A0" + "1" + "003" + "UU" + zmk_lmk.toUpperCase() + "X";
        return cmd;
    }

    public static String TranslateTAK_LMK2TAK_ZMK(String msgID, String zmk_lmk, String tak_lmk) {
        String cmd = CommonLib.formatToString(msgID, '0', 4) + "MGU" + zmk_lmk + "U" + tak_lmk + ";X01";
        return cmd;
    }

    public static String TranslateTAK_ZMK2TAK_LMK(String msgID, String zmk_lmk, String tak_zmk) {
        String cmd = CommonLib.formatToString(msgID, '0', 4) + "MIU" + zmk_lmk + "X" + tak_zmk + ";0U1";
        return cmd;
    }

    public static String GenerateMAC(String msgID, String tak_lmk, String data, HsmCommandEnum pCommandType) {
        String cmd = "";
        String dataLen;

        switch (pCommandType) {

            case BYTE:
                dataLen = CommonLib.formatToString(Integer.toHexString(data.length()), '0', 3).toUpperCase();
                cmd = CommonLib.formatToString(msgID, '0', 4) + "MKU" + tak_lmk + dataLen + data;
                break;

            case HEX:
            default:
                String dataInHex = CommonLib.asHex(data.getBytes());
                dataLen = CommonLib.formatToString(Integer.toHexString(dataInHex.length()), '0', 3).toUpperCase();
                cmd = CommonLib.formatToString(msgID, '0', 4) + "MKU" + tak_lmk + dataLen + dataInHex;
                break;
        }

        return cmd;
    }

    public static String VerifyMAC(String msgID, String tak_lmk, String mac, String dataLen, String data) {
        String cmd = CommonLib.formatToString(msgID, '0', 4) + "MMU" + tak_lmk + mac + dataLen + data;
        return cmd;
    }

    public static String EncryptDEK(String msgID, String zmk_lmk, String data) {
        String cmd = CommonLib.formatToString(msgID, '0', 4) + "M0" + "002100AU" + zmk_lmk + data;
        return cmd;
    }

    public static String DecryptDEK(String msgID, String zmk_lmk, String data) {
        String cmd = CommonLib.formatToString(msgID, '0', 4) + "M2" + "001200AU" + zmk_lmk + data;
        return cmd;
    }

    public static String getPinBlock(String msgID, String clearPin, String pan) {
        String panInHSM = pan.substring(pan.length() - 13, pan.length() - 1);
        while (clearPin.length() < 7) {
            clearPin = clearPin + "F";
        }
        String strPinBlockHSMCommand = CommonLib.formatToString(msgID, '0', 4) + "BA" + clearPin + panInHSM;
        return strPinBlockHSMCommand;
    }

    public static String getPinZPK(String msgID, String pan, String pinBlock, String zpk) {
        String panInHSM = pan.substring(pan.length() - 13, pan.length() - 1);
        String pninZPKCommand = CommonLib.formatToString(msgID, '0', 4) + "JG" + "U" + zpk + "01" + panInHSM + pinBlock;
        return pninZPKCommand;
    }

    public static String generateMACIST(String msgID, String zak_lmk, int dataLen, String data) {
        String cmd = (CommonLib.formatToString(msgID, '0', 4) + "MS" + "0110U" + zak_lmk
                + CommonLib.formatToString(Integer.toHexString(dataLen), '0', 4) + data).toUpperCase();
        return cmd;
    }
}
