/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package institutions.ist;

import cfg.cfgNode;
import ginterface.iInstitutionSecurity;
import hsm.hsmCmdObj;
import hsm.hsmLib;
import iso8583.IsoMessage;
import iso8583.IsoMessageType;
import iso8583.msgSecurity;
import iso8583.msgSecurityCommand;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;
import lib.CommonLib;
import lib.keyManager;
import lib.msgSecurityEnum;
import lib.secObjInfo;
import unisim201401.systemLoader;

/**
 *
 * @author netone
 */
public class ist15security implements iInstitutionSecurity {

    private systemLoader systemGlobalInfo;
    private String zone;

    public String getZone() {
        return zone;
    }

    public void setZone(String zone) {
        this.zone = zone;
    }

    public void setSystemGlobalInfo(systemLoader systemGlobalInfo) {
        this.systemGlobalInfo = systemGlobalInfo;
    }

    public Queue<secObjInfo> getSecurityList(IsoMessage pmsg) {
        Queue<secObjInfo> rs = new ConcurrentLinkedDeque<>();
        secObjInfo newSec;
        switch (pmsg.getMsgType()) {
            
            case REQUEST:
                if (systemGlobalInfo.getIssCfg().isRequireMac()) {
                    if ((!pmsg.getField(64).equals("")) || (!pmsg.getField(128).equals(""))) {

                        newSec = new secObjInfo(msgSecurityEnum.IN_NEED_OF_MACVER);
                        newSec.setMsgID(pmsg.getSeqID());
                        newSec.setHsmCommnadID(CommonLib.getHSMCommandID());
                        newSec.setdZone(pmsg.getDesInterfaceCode());
                        newSec.setsZone(pmsg.getSourceInterfaceCode());
                        rs.add(newSec);
                    }
                }
                newSec = new secObjInfo(msgSecurityEnum.MAKE_RESPONSE);
                newSec.setMsgID(pmsg.getSeqID());
                newSec.setdZone(pmsg.getDesInterfaceCode());
                newSec.setsZone(pmsg.getSourceInterfaceCode());
                rs.add(newSec);

                if (pmsg.getField(52).length() > 8) {
                    if (!pmsg.getSourceInterfaceCode().equals("SIMUI")) {
                        newSec = new secObjInfo(msgSecurityEnum.IN_NEED_OF_PIN);
                        newSec.setMsgID(pmsg.getSeqID());
                        newSec.setHsmCommnadID(CommonLib.getHSMCommandID());
                        newSec.setdZone(pmsg.getDesInterfaceCode());
                        newSec.setsZone(pmsg.getSourceInterfaceCode());
                        rs.add(newSec);
                    }

                } else {
                    if (pmsg.getField(52).length() >= 4) {
                        newSec = new secObjInfo(msgSecurityEnum.IN_NEED_GEN_PIN);
                        newSec.setMsgID(pmsg.getSeqID());
                        newSec.setHsmCommnadID(CommonLib.getHSMCommandID());
                        newSec.setdZone(pmsg.getDesInterfaceCode());
                        newSec.setsZone(pmsg.getSourceInterfaceCode());
                        rs.add(newSec);
                    }
                }

                if (systemGlobalInfo.getInstitutionDataConfig(pmsg.getDesInterfaceCode()).getValue("MAC").toUpperCase().equals("YES")) {
                    newSec = new secObjInfo(msgSecurityEnum.IN_NEED_OF_MACGEN);
                    newSec.setMsgID(pmsg.getSeqID());
                    newSec.setHsmCommnadID(CommonLib.getHSMCommandID());
                    newSec.setdZone(pmsg.getDesInterfaceCode());
                    newSec.setsZone(pmsg.getSourceInterfaceCode());
                    rs.add(newSec);
                }
                break;
            case RESPONSE:

                if (systemGlobalInfo.getInstitutionDataConfig(pmsg.getDesInterfaceCode()).getValue("MAC").toUpperCase().equals("YES")) {
                    newSec = new secObjInfo(msgSecurityEnum.IN_NEED_OF_MACGEN);
                    newSec.setMsgID(pmsg.getSeqID());
                    newSec.setHsmCommnadID(CommonLib.getHSMCommandID());
                    newSec.setdZone(pmsg.getDesInterfaceCode());
                    newSec.setsZone(pmsg.getSourceInterfaceCode());
                    rs.add(newSec);
                }
                break;
            case NETWORK_REQUEST:
                switch (CommonLib.valueOf(pmsg.getField(70))) {
                    case 161: //new key
                        if (pmsg.getField(48).indexOf("MAK") >= 0) {
                            newSec = new secObjInfo(msgSecurityEnum.NET_TAK_TRANSLATE_ZMK_LMK);

                        } else {
                            newSec = new secObjInfo(msgSecurityEnum.NET_ZPK_TRASLATE_ZMK_LMK);

                        }

                        newSec.setMsgID(pmsg.getSeqID());
                        newSec.setdZone(pmsg.getDesInterfaceCode());
                        newSec.setsZone(pmsg.getSourceInterfaceCode());
                        rs.add(newSec);

                }

                newSec = new secObjInfo(msgSecurityEnum.MAKE_RESPONSE);
                newSec.setMsgID(pmsg.getSeqID());
                newSec.setdZone(pmsg.getDesInterfaceCode());
                newSec.setsZone(pmsg.getSourceInterfaceCode());
                rs.add(newSec);
                break;
            case NETWORK_RESPONSE:
                break;
        }
        /*switch (systemGlobalInfo.getInstitutionDataConfig(pmsg.getDesInterfaceCode()).getIntValue("TYPE")) {
         case 10:
         case 1://IST

         break;
         }*/
        return rs;

    }

    @Override
    public IsoMessage updateSecurity(IsoMessage pmsg, msgSecurity pSec) {
        IsoMessage rs = new IsoMessage(pmsg);
        switch (pSec.getMsgSecType()) {
            case IN_NEED_OF_MACGEN:

                break;
            case IN_NEED_OF_MACVER:

                break;
            case IN_NEED_GEN_PIN:

                break;
            case IN_NEED_GEN_PIN_ZPK:
            case IN_NEED_OF_PIN:
                rs.setField(52, pSec.getHSMReturnValue());
                break;
            case NET_TAK_GENERATE_ZMK:

                break;
            case NET_TAK_TRANSLATE_ZMK_LMK:
                break;

            case NET_ZPK_GENERATE_ZMK:
                break;
            case NET_ZPK_TRASLATE_ZMK_LMK:
                break;

        }
        return rs;
    }

    @Override
    public hsmCmdObj getSecCommand(IsoMessage pmsg, secObjInfo pSecType) {

        hsmCmdObj hsmObjRs = new hsmCmdObj();
        hsmObjRs.setHsmCommandID(CommonLib.valueOf(pSecType.getHsmCommnadID()));
        hsmObjRs.setMsgType(pSecType.getTypeOfSec());
        hsmObjRs.setHsmCommandID(CommonLib.valueOf(pSecType.getHsmCommnadID()));
        switch (pSecType.getTypeOfSec()) {
            case IN_NEED_OF_MACGEN:
            case IN_NEED_OF_MACVER:
                //String macFieldList = "0,2,3,4,7,11,32,38,39,41,42,48,90,95,102,103";
                String macFieldList = systemGlobalInfo.getInstitutionDataConfig(pmsg.getDesInterfaceCode()).getValue("MACFIELDS");
                String[] macFields = macFieldList.split(",");
                String macString = "";
                for (int i = 0; i < macFields.length; i++) {
                    macString += pmsg.getField(CommonLib.valueOf(macFields[i]));
                }

                hsmObjRs.setCommandHSM(hsmLib.generateMACIST(CommonLib.formatToString(String.valueOf(pSecType.getHsmCommnadID()), '0', 4), keyManager.getTAK(pmsg.getDesInterfaceCode(), systemGlobalInfo), macString.length(), macString));

                break;
            case IN_NEED_GEN_PIN:
                hsmObjRs.setCommandHSM(hsmLib.getPinBlock(CommonLib.formatToString(String.valueOf(pSecType.getHsmCommnadID()), '0', 4), pmsg.getField(CommonLib.valueOf(pSecType.getFields()[0])), pmsg.getField(2)));
                break;
            case IN_NEED_GEN_PIN_ZPK:
                hsmObjRs.setCommandHSM(hsmLib.getPinZPK(CommonLib.formatToString(String.valueOf(pSecType.getHsmCommnadID()), '0', 4), pmsg.getField(2), pSecType.getFields()[0], keyManager.getZPK(pmsg.getDesInterfaceCode(), systemGlobalInfo)));
                break;
            case IN_NEED_OF_PIN:
                String acc = pmsg.getField(2).substring(pmsg.getField(2).length() - 13, pmsg.getField(2).length() - 1);
                hsmObjRs.setCommandHSM(
                        hsmLib.TranslatePIN(
                                CommonLib.formatToString(String.valueOf(pSecType.getHsmCommnadID()), '0', 4),
                                pmsg.getField(52), acc, keyManager.getInZPK(pmsg.getSourceInterfaceCode(), systemGlobalInfo),
                                keyManager.getOutZPK(pmsg.getDesInterfaceCode(), systemGlobalInfo)));

                break;
            case NET_TAK_GENERATE_ZMK:

                break;
            case NET_TAK_TRANSLATE_ZMK_LMK:
                if (pmsg.getField(48).indexOf("MAK") >= 0) {
                    hsmObjRs.setCommandHSM(
                            hsmLib.TranslateTAK_ZMK2TAK_LMK(
                                    CommonLib.formatToString(String.valueOf(pSecType.getHsmCommnadID()), '0', 4),
                                    keyManager.getZMK(pmsg.getSourceInterfaceCode(), systemGlobalInfo),
                                    pmsg.getField(48).substring(6)));
                }
                break;

            case NET_ZPK_GENERATE_ZMK:

                hsmObjRs.setCommandHSM(hsmLib.GenerateZPK(String.valueOf(pSecType.getHsmCommnadID()), keyManager.getZMK(pmsg.getDesInterfaceCode(), systemGlobalInfo)));
                break;
            case NET_ZPK_TRASLATE_ZMK_LMK:
                if (pmsg.getField(48).indexOf("ZPK") >= 0) {
                    hsmObjRs.setCommandHSM(
                            hsmLib.TranslateZPK_ZMK2ZPK_LMK(
                                    CommonLib.formatToString(String.valueOf(pSecType.getHsmCommnadID()), '0', 4),
                                    keyManager.getZMK(pmsg.getSourceInterfaceCode(), systemGlobalInfo),
                                    pmsg.getField(48).substring(6)));
                } else {
                    hsmObjRs.setCommandHSM(
                            hsmLib.TranslateZPK_ZMK2ZPK_LMK(
                                    CommonLib.formatToString(String.valueOf(pSecType.getHsmCommnadID()), '0', 4),
                                    keyManager.getZMK(pmsg.getSourceInterfaceCode(), systemGlobalInfo),
                                    pmsg.getField(48)));
                }
                break;

        }
        return hsmObjRs;

    }
}
