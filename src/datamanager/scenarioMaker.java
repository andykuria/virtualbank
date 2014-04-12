/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package datamanager;

import cfg.cfgNode;
import cfg.cfgParser;
import globalutils.scriptEnum;
import iso8583.IsoMessage;
import iso8583.IsoMessageType;
import java.util.AbstractList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.swing.JTextField;
import lib.CommonLib;
import lib.DateUtils;
import lib.msgSecurityEnum;
import lib.secObjInfo;

/**
 *
 * @author netone
 */
public class scenarioMaker {

    List<IsoMessage> listOfMsg = new LinkedList<>();
    cfgParser scenarioPattern;
    Map<String, String> pinValue = new ConcurrentHashMap<>();
    String desInstitution;

    Map<String, Map<String, fieldParser>> allPattern;
    List<String> cardList;
    cfgNode isoCfg;

    public void setIsoCfg(cfgNode isoCfg) {
        this.isoCfg = isoCfg;
    }

    
    public scenarioMaker(cfgParser scenarioPattern) {
        this.scenarioPattern = scenarioPattern;
        initPattern();
        cardList = new LinkedList<>();
        cardList.addAll(Arrays.asList(scenarioPattern.getValue("HEADER", "CL").split(",")));

    }

    public String getDesInstitution() {
        return desInstitution;
    }

    public void setDesInstitution(String desInstitution) {
        this.desInstitution = desInstitution;
    }

    public Map<String, String> getPinValue() {
        return pinValue;
    }

    private void initPattern() {
        List<cfgNode> nodes = scenarioPattern.getReverseNodeType("HEADER,SCENARIO");
        allPattern = new LinkedHashMap<>();
        for (cfgNode iNode : nodes) {
            Map<String, fieldParser> msgPatternParser = iNode.getFieldPatternFromNode();
            allPattern.put(iNode.getNodeID(), msgPatternParser);
        }
    }

    public void buildMessages() {
        listOfMsg = new LinkedList<>();
        cfgNode msgScript = scenarioPattern.getXmlNode("SCRIPT");
        List<String> scriptList = msgScript.getFieldKeys();
        int delayTime=0;
        for (String scriptid : scriptList) {
            scriptParser sp = new scriptParser(msgScript.getValue(scriptid));
            IsoMessage msgTmp = genMsgFromParretn(sp);
            delayTime+=msgTmp.getDelaytime();
            msgTmp.setDelaytime(delayTime);
            listOfMsg.add(msgTmp);
        }

    }

    public List<IsoMessage> getListOfMsg() {
        return listOfMsg;
    }

    private IsoMessage genMsgFromParretn(scriptParser sP) {
        IsoMessage rs = new IsoMessage();
        rs.setSourceInterfaceCode("SIMUI");
        rs.setDesInterfaceCode(desInstitution);
        rs.setIsoCfg(isoCfg);

        rs.setDelaytime(sP.getDelayTime());
        Map<String, fieldParser> ptn = allPattern.get(sP.getMsgId());
        if (ptn != null) {
            IsoMessage originalTranx = null;
            if (sP.getTypeOfScript() == scriptEnum.REVERSAL) {
                originalTranx = listOfMsg.get(CommonLib.valueOf(sP.getValue("OR")) - 1);
            }

            for (String iField : ptn.keySet()) {

                switch (ptn.get(iField).getType()) {
                    case AUTO_ORIGINAL:
                        rs.setField(CommonLib.valueOf(iField), originalTranx.getField(CommonLib.valueOf(iField)));
                        break;
                    case AUTO_DATE:
                        rs.setField(CommonLib.valueOf(iField), DateUtils.getCurrentDateIST());
                        break;
                    case AUTO_DATETIME:
                        rs.setField(CommonLib.valueOf(iField), DateUtils.getCurrentDateTime());
                        break;
                    case AUTO_PAN:
                        int iPan = (CommonLib.valueOf(ptn.get(iField).getFieldValue()) > cardList.size()) ? 0 : CommonLib.valueOf(ptn.get(iField).getFieldValue());
                        String track2 = cardList.get(iPan);
                        rs.setField(CommonLib.valueOf(iField), track2.substring(0, track2.indexOf("=")));
                        break;
                    case AUTO_TRACK2:
                        iPan = (CommonLib.valueOf(ptn.get(iField).getFieldValue()) > cardList.size()) ? 0 : CommonLib.valueOf(ptn.get(iField).getFieldValue());
                        track2 = cardList.get(iPan);
                        rs.setField(CommonLib.valueOf(iField), track2);
                        break;
                    case AUTO_TIME:
                        rs.setField(CommonLib.valueOf(iField), DateUtils.getTime());
                        break;
                    case AUTO_HSM:
                        secObjInfo pinCmdReq = new secObjInfo(msgSecurityEnum.IN_NEED_GEN_PIN);
                        
                        pinCmdReq.setHsmCommnadID(CommonLib.getHSMCommandID());
                        pinCmdReq.setMsgID(rs.getSeqID());
                        pinCmdReq.setsZone(rs.getSourceInterfaceCode());
                        pinCmdReq.setdZone(rs.getDesInterfaceCode());
                        rs.addSecRequest(pinCmdReq);
                        break;
                    case AUTO_SEQ37:
                        rs.setField(CommonLib.valueOf(iField), CommonLib.getRefNo());
                        break;
                    case AUTO_TRACE:
                        rs.setField(CommonLib.valueOf(iField), CommonLib.getSystemTrace());
                        break;
                    case AUTO_BITMAP:
                        break;
                    case AUTO_AMMOUNT:
                        String[] amms = ptn.get(iField).getFieldValue().split("-");
                        int minAmm = CommonLib.valueOf(amms[0]);
                        int maxAmm = CommonLib.valueOf(amms[1]);
                        int digit = CommonLib.valueOf(amms[2]);
                        rs.setField(CommonLib.valueOf(iField), CommonLib.getAmmount(minAmm, maxAmm, digit, 12));

                        break;
                    case AUTO_MAC_GEN:
                        secObjInfo maxCmdReq = new secObjInfo(msgSecurityEnum.IN_NEED_OF_MACGEN);
                        if (iField.equals("64")) {
                            maxCmdReq.setFields(new String[]{"64"});

                        } else {
                            maxCmdReq.setFields(new String[]{"128"});
                        }
                        maxCmdReq.setHsmCommnadID(CommonLib.getHSMCommandID());
                        maxCmdReq.setMsgID(rs.getSeqID());
                        maxCmdReq.setsZone(rs.getSourceInterfaceCode());
                        maxCmdReq.setdZone(rs.getDesInterfaceCode());
                        rs.addSecRequest(maxCmdReq);
                        break;
                    default:
                        rs.setField(CommonLib.valueOf(iField), ptn.get(iField).getFieldValue());
                }
            }

        }
        rs.setMessageState(true);
        return rs;

    }

}
