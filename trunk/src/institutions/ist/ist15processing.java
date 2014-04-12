/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package institutions.ist;

import cfg.cfgNode;
import cfg.nodeType;
import datamanager.carddataLoader;
import datamanager.fieldParser;
import datamanager.fieldType;
import datamanager.messagePatternLoader;
import iss.iIssProcessing;
import iso8583.IsoMessage;
import iso8583.msg8583Type;
import iss.issSettings;
import java.util.List;
import lib.CommonLib;
import lib.DateUtils;
import lib.msgSecurityEnum;
import lib.secObjInfo;
import org.w3c.dom.Element;
import unisim201401.systemLoader;

/**
 *
 * @author minhdbh
 */
public class ist15processing implements iIssProcessing {

    private messagePatternLoader carddata;
    private messagePatternLoader issFormat;
    private String instScope = "";
    private systemLoader systemGlobalInfo;

    public void setSystemGlobalInfo(systemLoader systemGlobalInfo) {
        this.systemGlobalInfo = systemGlobalInfo;
    }

    @Override
    public void setCards(messagePatternLoader cardList) {
        this.carddata = cardList;
    }

    @Override
    public void setIssResponseFormat(messagePatternLoader issResponseFormat) {
        this.issFormat = issResponseFormat;
    }

    public cfgNode checkIssData(IsoMessage requestMsg) {
        if (carddata != null) {
            List<cfgNode> listofcard = carddata.getCardData(requestMsg.getField(2), requestMsg.getDesInterfaceCode());;
            if (listofcard.size() > 0) {
                return listofcard.get(0);
            }
        }
        return null;

    }

    @Override
    public IsoMessage getResponse(IsoMessage requestMsg) {
        IsoMessage rs = new IsoMessage();
        try {
            carddataLoader cd = new carddataLoader(carddata);
            cd.setInstCode(instScope);
            List<cfgNode> alLFmt = issFormat.getNode(String.valueOf(requestMsg.getType()));
            cfgNode responseFmt = null;
            if (alLFmt.size() > 0) {
                responseFmt = alLFmt.get(0);
            }
            switch (requestMsg.getType()) {
                case BAL:
                    if (checkIssData(requestMsg) != null) {
                        rs = processBI(requestMsg, responseFmt, cd);
                        rs.setMsgType(CommonLib.getMsgType(rs.getField(0)));
                        rs.setSecRequest(systemGlobalInfo.getSecurityUtils(rs.getDesInterfaceCode()));
                        rs.setDelaytime(systemGlobalInfo.getIssCfg().getDelayTime());
                    } else {
                        rs = requestMsg;
                    }
                    break;
                case CW:
                    /*issAccount = cd.getCardInf(requestMsg.getField(2));
                     balanceAmmount = issAccount.getIntValue("AvaiBalance");
                     accBalance = issAccount.getValue("Balance");
                     Integer transAmm = CommonLib.valueOf(requestMsg.getField(4));
                     Integer creditAmm = 0;
                     if (balanceAmmount >= transAmm) {
                     creditAmm = balanceAmmount - transAmm;
                     }
                     String creditAmminStr = CommonLib.formatIntToString(creditAmm, issAccount.getValue("AvaiBalance").length());
                     if (responseFmt != null) {
                     List<String> fieldInFmt = responseFmt.getFieldKeys();
                     for (String iFieldFMT : fieldInFmt) {
                     fieldParser parseFieldi = new fieldParser(responseFmt.getValue(iFieldFMT));
                     switch (parseFieldi.getType()) {
                     case AUTO_DATE:
                     rs.setField(CommonLib.valueOf(iFieldFMT), DateUtils.getCurrentDate());
                     break;
                     case AUTO_DATETIME:
                     rs.setField(CommonLib.valueOf(iFieldFMT), DateUtils.getCurrentDateTime());
                     break;
                     case AUTO_TIME:
                     rs.setField(CommonLib.valueOf(iFieldFMT), DateUtils.getTime());
                     break;
                     case AUTO_ORIGINAL:
                     rs.setField(CommonLib.valueOf(iFieldFMT), requestMsg.getField(CommonLib.valueOf(iFieldFMT)));
                     break;
                     case TMP_ACC:

                     String accRs = parseFieldi.getFieldValue().replace("{AB}", issAccount.getValue("AvaiBalance")).replace("{B}", issAccount.getValue("Balance"));

                     rs.setField(CommonLib.valueOf(iFieldFMT), accRs);
                     break;

                     default:
                     rs.setField(CommonLib.valueOf(iFieldFMT), parseFieldi.getFieldValue());

                     }
                     }

                     }
                     switch (issAccount.getIntValue("RC")) {
                     case 0:
                     rs.setField(54, creditAmminStr);
                     break;
                     default:
                     rs.setField(54, "0000000000000000000000000000000000000000");

                     }*/
                    if (checkIssData(requestMsg) != null) {
                        rs = processCW(requestMsg, responseFmt, cd);
                        rs.setMsgType(CommonLib.getMsgType(rs.getField(0)));
                        rs.setSecRequest(systemGlobalInfo.getSecurityUtils(rs.getDesInterfaceCode()));
                        rs.setDelaytime(systemGlobalInfo.getIssCfg().getDelayTime());
                    } else {
                        rs = requestMsg;
                    }
                    break;
                case SIGNON:
                case SIGNOFF:
                case ECHO:

                case NEWKEY:

                    rs = processAutoResponse(requestMsg, responseFmt);
                case REVERSAL:
                    rs.setDelaytime(systemGlobalInfo.getIssCfg().getDelayTime());
                    break;

                case UNKNOWN:

                    break;

            }
        } catch (Exception ex) {
        }

        return rs;
    }

    private IsoMessage processAutoResponse(IsoMessage requestMsg, cfgNode responseFmt) {
        IsoMessage rs = new IsoMessage();
        rs.setSourceInterfaceCode(requestMsg.getDesInterfaceCode());
        rs.setDesInterfaceCode(requestMsg.getSourceInterfaceCode());
        rs.setIsoCfg(systemGlobalInfo.getIsoFormatByScope(rs.getSourceInterfaceCode()));
        if (responseFmt != null) {
            List<String> fieldInFmt = responseFmt.getFieldKeys();
            for (String iFieldFMT : fieldInFmt) {
                fieldParser parseFieldi = new fieldParser(responseFmt.getValue(iFieldFMT));
                switch (parseFieldi.getType()) {
                    case AUTO_DATE:
                        rs.setField(CommonLib.valueOf(iFieldFMT), DateUtils.getCurrentDate());
                        break;
                    case AUTO_DATETIME:
                        rs.setField(CommonLib.valueOf(iFieldFMT), DateUtils.getCurrentDateTime());
                        break;
                    case AUTO_TIME:
                        rs.setField(CommonLib.valueOf(iFieldFMT), DateUtils.getTime());
                        break;
                    case AUTO_ORIGINAL:
                        rs.setField(CommonLib.valueOf(iFieldFMT), requestMsg.getField(CommonLib.valueOf(iFieldFMT)));
                        break;

                    case AUTO_BITMAP:
                        break;
                    default:
                        rs.setField(CommonLib.valueOf(iFieldFMT), parseFieldi.getFieldValue());

                }
            }

        }
        return rs;
    }

    private IsoMessage processBI(IsoMessage requestMsg, cfgNode responseFmt, carddataLoader cd) {
        IsoMessage rs = new IsoMessage();
        rs.setDesInterfaceCode(requestMsg.getSourceInterfaceCode());
        rs.setSourceInterfaceCode(requestMsg.getDesInterfaceCode());
        rs.setIsoCfg(systemGlobalInfo.getIsoFormatByScope(rs.getSourceInterfaceCode()));
        cfgNode issAccount = cd.getCardInf(requestMsg.getField(2));
        Integer balanceAmmount = issAccount.getIntValue("AvaiBalance");
        String accBalance = issAccount.getValue("Balance");

        if (responseFmt != null) {
            List<String> fieldInFmt = responseFmt.getFieldKeys();

            for (String iFieldFMT : fieldInFmt) {
                fieldParser parseFieldi = new fieldParser(responseFmt.getValue(iFieldFMT));
                switch (parseFieldi.getType()) {
                    case AUTO_DATE:
                        rs.setField(CommonLib.valueOf(iFieldFMT), DateUtils.getCurrentDate());
                        break;
                    case AUTO_DATETIME:
                        rs.setField(CommonLib.valueOf(iFieldFMT), DateUtils.getCurrentDateTime());
                        break;
                    case AUTO_TIME:
                        rs.setField(CommonLib.valueOf(iFieldFMT), DateUtils.getTime());
                        break;
                    case AUTO_ORIGINAL:
                        rs.setField(CommonLib.valueOf(iFieldFMT), requestMsg.getField(CommonLib.valueOf(iFieldFMT)));
                        break;
                    case AUTO_39:
                        rs.setField(CommonLib.valueOf(iFieldFMT), issAccount.getValue("RC"));
                        break;
                    case TMP_ACC:

                        String accRs = parseFieldi.getFieldValue().replace("{AB}", issAccount.getValue("AvaiBalance")).replace("{B}", issAccount.getValue("Balance"));

                        rs.setField(CommonLib.valueOf(iFieldFMT), accRs);
                        break;
                    case FIXXED_VALUE:
                        rs.setField(CommonLib.valueOf(iFieldFMT), parseFieldi.getFieldValue());
                        break;
                    case AUTO_BITMAP:
                    default:

                }
            }

            rs.setMessageState(true);

        }
        if (rs.isMessage()) {
            switch (systemGlobalInfo.getIssCfg().getRc().getType()) {
                case AUTO:
                    switch (issAccount.getIntValue("RC")) {
                        case 0:

                            break;
                        default:
                            rs.setField(54, "0000000000000000000000000000000000000000");

                    }
                    break;
                case FIXXED:
                    rs.setField(39, systemGlobalInfo.getIssCfg().getRc().getValueForFix());
                    rs.setField(54, "0000000000000000000000000000000000000000");

                    break;
            }

            switch (systemGlobalInfo.getIssCfg().getAuth().getType()) {
                case AUTO:
                    rs.setField(38, issAccount.getValue("AUTHCODE"));
                    break;
                case FIXXED:
                    rs.setField(38, systemGlobalInfo.getIssCfg().getRc().getValueForFix());
                    break;
                case NONE:
                    rs.remove(38);
                    break;
            }
        }
        rs.setDesInterfaceCode(requestMsg.getSourceInterfaceCode());
        rs.setSourceInterfaceCode(requestMsg.getDesInterfaceCode());
        //rs.setSecRequest(systemGlobalInfo.getSecurityUtils(rs.getDesInterfaceCode()).getSecurityList(rs));
        return rs;
    }

    private IsoMessage processCW(IsoMessage requestMsg, cfgNode responseFmt, carddataLoader cd) {
        IsoMessage rs = new IsoMessage();
        rs.setDesInterfaceCode(requestMsg.getSourceInterfaceCode());
        rs.setSourceInterfaceCode(requestMsg.getDesInterfaceCode());
        rs.setIsoCfg(systemGlobalInfo.getIsoFormatByScope(rs.getSourceInterfaceCode()));
        cfgNode issAccount = cd.getCardInf(requestMsg.getField(2));
        Integer balanceAmmount = issAccount.getIntValue("AvaiBalance");
        String accBalance = issAccount.getValue("Balance");
        Integer transAmm = CommonLib.valueOf(requestMsg.getField(4));
        Integer creditAmm = 0;
        if (balanceAmmount >= transAmm) {
            creditAmm = balanceAmmount - transAmm;
        }
        String creditAmminStr = CommonLib.formatIntToString(creditAmm, issAccount.getValue("AvaiBalance").length());
        if (responseFmt != null) {
            List<String> fieldInFmt = responseFmt.getFieldKeys();
            for (String iFieldFMT : fieldInFmt) {
                fieldParser parseFieldi = new fieldParser(responseFmt.getValue(iFieldFMT));
                switch (parseFieldi.getType()) {
                    case AUTO_DATE:
                        rs.setField(CommonLib.valueOf(iFieldFMT), DateUtils.getCurrentDate());
                        break;
                    case AUTO_DATETIME:
                        rs.setField(CommonLib.valueOf(iFieldFMT), DateUtils.getCurrentDateTime());
                        break;
                    case AUTO_TIME:
                        rs.setField(CommonLib.valueOf(iFieldFMT), DateUtils.getTime());
                        break;
                    case AUTO_ORIGINAL:
                        rs.setField(CommonLib.valueOf(iFieldFMT), requestMsg.getField(CommonLib.valueOf(iFieldFMT)));
                        break;
                    case TMP_ACC:

                        String accRs = parseFieldi.getFieldValue().replace("{AB}", issAccount.getValue("AvaiBalance")).replace("{B}", issAccount.getValue("Balance"));

                        rs.setField(CommonLib.valueOf(iFieldFMT), accRs);
                        break;
                    case AUTO_39:
                        rs.setField(CommonLib.valueOf(iFieldFMT), issAccount.getValue("RC"));
                        break;
                    case FIXXED_VALUE:
                        rs.setField(CommonLib.valueOf(iFieldFMT), parseFieldi.getFieldValue());
                        break;
                    case AUTO_BITMAP:
                    default:
                        break;

                }
            }
            rs.setMessageState(true);

        }

        if (rs.isMessage()) {
            switch (systemGlobalInfo.getIssCfg().getRc().getType()) {
                case AUTO:
                    switch (issAccount.getIntValue("RC")) {
                        case 0:

                            break;
                        default:
                            rs.setField(54, "0000000000000000000000000000000000000000");

                    }
                    break;
                case FIXXED:
                    rs.setField(39, systemGlobalInfo.getIssCfg().getRc().getValueForFix());
                    rs.setField(54, "0000000000000000000000000000000000000000");

                    break;
            }

            switch (systemGlobalInfo.getIssCfg().getAuth().getType()) {
                case AUTO:
                    rs.setField(38, issAccount.getValue("AUTHCODE"));
                    break;
                case FIXXED:
                    rs.setField(38, systemGlobalInfo.getIssCfg().getRc().getValueForFix());
                    break;
                case NONE:
                    rs.remove(38);
                    break;
            }
        }
        rs.setDesInterfaceCode(requestMsg.getSourceInterfaceCode());
        rs.setSourceInterfaceCode(requestMsg.getDesInterfaceCode());
        //rs.setSecRequest(systemGlobalInfo.getSecurityUtils(rs.getDesInterfaceCode()));
        return rs;

    }

    @Override
    public void setInstScope(String pIns) {
        this.instScope = pIns;
    }

    @Override
    public String getInstScope() {
        return instScope;
    }

    @Override
    public IsoMessage makeRevFromFin(IsoMessage requestMsg, cfgNode revFmt) {

        IsoMessage rs = new IsoMessage();
        rs.setIsoCfg(systemGlobalInfo.getIsoFormatByScope(requestMsg.getDesInterfaceCode()));
        rs.setSourceInterfaceCode(requestMsg.getSourceInterfaceCode());
        rs.setDesInterfaceCode(requestMsg.getDesInterfaceCode());

        //List<cfgNode> alLFmt = systemGlobalInfo.getPatternObj().getNodeByType(nodeType.REVERSAL);
        //cfgNode revFmt = null;
        //if (alLFmt.size() > 0) {
        //    revFmt = alLFmt.get(0);
        //}
        if (revFmt != null) {
            List<String> fieldInFmt = revFmt.getFieldKeys();

            for (String iFieldFMT : fieldInFmt) {
                fieldParser parseFieldi = new fieldParser(revFmt.getValue(iFieldFMT));
                switch (parseFieldi.getType()) {
                    case AUTO_DATE:
                        rs.setField(CommonLib.valueOf(iFieldFMT), DateUtils.getCurrentDate());
                        break;
                    case AUTO_DATETIME:
                        rs.setField(CommonLib.valueOf(iFieldFMT), DateUtils.getCurrentDateTime());
                        break;
                    case AUTO_TIME:
                        rs.setField(CommonLib.valueOf(iFieldFMT), DateUtils.getTime());
                        break;
                    case AUTO_ORIGINAL:
                        rs.setField(CommonLib.valueOf(iFieldFMT), requestMsg.getField(CommonLib.valueOf(iFieldFMT)));
                        break;
                    case FIXXED_VALUE:
                        rs.setField(CommonLib.valueOf(iFieldFMT), parseFieldi.getFieldValue());
                        break;
                    case AUTO_BITMAP:
                    default:

                }
            }

            rs.setMessageState(true);

        }

        rs.setSourceInterfaceCode(requestMsg.getSourceInterfaceCode());
        rs.setDesInterfaceCode(requestMsg.getDesInterfaceCode());
        rs.setMsgType(CommonLib.getMsgType(rs.getField(0)));
        rs.setSecRequest(systemGlobalInfo.getSecurityUtils(rs.getDesInterfaceCode()));
        return rs;
    }
}
