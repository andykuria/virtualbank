/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package unisim201401;

import cfg.*;
import connection.connectionProfile;
import connection.hsmConnections;
import datamanager.messagePatternLoader;
import ginterface.iInstitutionSecurity;

import ginterface.iqueryactionable;

import globalutils.ConfigInfo;
import globalutils.ConfigType;
import globalutils.routingEnum;
import globalutils.systemconfig;
import institutions.ist.ist15processing;

import institutions.ist.ist15security;
import institutions.ist.istdata;

import iso8583.ConfigIsoMessage;

import java.util.logging.Level;
import java.util.logging.Logger;

import iso8583.HeaderProcessing;
import iso8583.IsoMessage;
import iss.SimParas;
import iss.iIssProcessing;
import iss.issSettings;
import java.util.List;
import javax.swing.JTextArea;
import lib.seqManager;
import processing.dataDictionary;
import processing.delayTube;
import processing.hsmCommandQueue;

import processing.hsmSecQueue;
import processing.networkConfigQueue;
import processing.secObjQueue;
import processing.systemMessageFlowControl;
import processing.systemMessageQueue;
import processing.systemMessageSecurityQueue;

/**
 *
 * @author minhdbh
 */
public class systemLoader {

    private seqManager sequencyService;
    private JTextArea taLogs;
    private issSettings issCfg;
    private cfgParser systemCfg;
    private cfgParser routingCfg;
    private messagePatternLoader issCardData;
    private messagePatternLoader issResformat;
    private messagePatternLoader patternLoader;
    private messagePatternLoader isoFormatLodaer;
    private cfgParser[] instCfg;
    private iInstitutionSecurity[] securityUtils;
    private connectionProfile[] cnnProfile;
    private iIssProcessing[] issResponse;
    private hsmConnections hsmCnns;
    private ConfigIsoMessage[] isoCfg;
    private SimParas sParas = new SimParas();
    private dataDictionary<IsoMessage> reversalMap;
    private dataDictionary<IsoMessage> originalMap;
    private delayTube delayQueue;
    /*private imsgincomming[] icmQueue;
     private imsgoutgoing[] outQueue;*/
    private iqueryactionable[] institutionData;
    //private DBConnProfile[] dbConn;
    private systemMessageQueue icmQueue;
    private systemMessageQueue outQueue;
    private hsmSecQueue mhsmQueue;
    private secObjQueue msecObjQueue;
    private hsmCommandQueue hsmCmdQueue;
    private systemMessageSecurityQueue securityQueue;
    private systemMessageFlowControl flowControlQueue;
    private networkConfigQueue cfgQueue;

    public void initSystemConfig() {

        sequencyService=new seqManager();
        reversalMap=new dataDictionary<>();
        originalMap=new dataDictionary<>();
        
        systemconfig.SystemTrace=sequencyService.getSeq6();
        systemconfig.de37=sequencyService.getSeq12();
        icmQueue = new systemMessageQueue();
        outQueue = new systemMessageQueue();
        mhsmQueue = new hsmSecQueue();
        msecObjQueue = new secObjQueue();
        hsmCmdQueue = new hsmCommandQueue();
        securityQueue = new systemMessageSecurityQueue();
        flowControlQueue = new systemMessageFlowControl();
        cfgQueue = new networkConfigQueue();

        msecObjQueue.setHsmQueue(hsmCmdQueue);
        msecObjQueue.setSmSecQueue(securityQueue);
        msecObjQueue.setSystemGlobalInfo(this);
        msecObjQueue.setMsgFlowControl(flowControlQueue);

        systemCfg = new cfgParser("cfg/bics_system.xml");
        systemCfg.setSystemGlobal(this);

        cfgQueue = new networkConfigQueue();

        routingCfg = new cfgParser();
        routingCfg.setSystemGlobal(this);
        routingCfg.ParseConfig("cfg/routing_table.xml");

        patternLoader = new messagePatternLoader();
        patternLoader.setDirectoryPath("cfg/data");
        patternLoader.init();

        isoFormatLodaer = new messagePatternLoader();
        isoFormatLodaer.setDirectoryPath("cfg/isoformat");
        isoFormatLodaer.init();

        issResformat = new messagePatternLoader("cfg/iss");
        issResformat.init();

        issCardData = new messagePatternLoader("cfg/iss/cards");
        issCardData.init();

        cfgNode instProfile = systemCfg.getXmlNode("BANK-PROFILE");

        instCfg = new cfgParser[instProfile.getSize()];
        List<String> insKeys = instProfile.getFieldKeys();

        securityUtils = new iInstitutionSecurity[instProfile.getSize()];
        institutionData = new iqueryactionable[instProfile.getSize()];
        issResponse = new iIssProcessing[instProfile.getSize()];
        for (int i = 0; i < instProfile.getSize(); i++) {
            instCfg[i] = new cfgParser("cfg/banks/" + instProfile.getValue(insKeys.get(i)));

            switch (instCfg[i].getIntValue("INSTITUTION", "TYPE")) {
                case 1: //IST1.4
                    break;
                case 10: //IST 1.5
                    securityUtils[i] = new ist15security();
                    securityUtils[i].setZone(instCfg[i].getValue("INSTITUTION", "INTERFACECODE"));
                    securityUtils[i].setSystemGlobalInfo(this);

                    institutionData[i] = new istdata();
                    institutionData[i].setInstConfig(instCfg[i].getXmlNode("INSTITUTION"));

                    issResponse[i] = new ist15processing();
                    issResponse[i].setInstScope(instCfg[i].getValue("INSTITUTION", "INTERFACECODE"));

                    issResponse[i].setIssResponseFormat(issResformat);
                    issResponse[i].setCards(issCardData);
                    issResponse[i].setSystemGlobalInfo(this);
                    break;
            }
        }
        hsmCnns = new hsmConnections();
        hsmCnns.setNumberCnn(systemCfg.getIntValue("HSM", "Connections"));
        hsmCnns.setServer(systemCfg.getValue("HSM", "IP"));
        hsmCnns.setPort(systemCfg.getIntValue("HSM", "PORT"));
        hsmCnns.setSystemSecQueue(msecObjQueue);
        hsmCnns.setSystemGlobal(this);
        issCfg = new issSettings();
        delayQueue=new delayTube();
    }

    public iInstitutionSecurity getSecurityUtils(String zone) {
        for (int i = 0; i < securityUtils.length; i++) {
            if (securityUtils[i].getZone().toUpperCase().equals(zone)) {
                return securityUtils[i];
            }
        }
        return null;
    }

    public void startHsmConnector(int pmilisecound) {
        hsmCnns.initHSM();

    }

    public cfgNode getIsoFormatByScope(String pScope) {
        return isoFormatLodaer.getNodeNotInScope("HEADER", pScope).get(0);
    }

    public void startInstConnector(Thread pparentThread) {
        try {
            Thread.sleep(100);
        } catch (InterruptedException ex) {
            Logger.getLogger(systemLoader.class.getName()).log(Level.SEVERE, null, ex);
        }
        // connectionProfile
        cnnProfile = new connectionProfile[instCfg.length];

        for (int iConnector = 0; iConnector < instCfg.length; iConnector++) {
            cnnProfile[iConnector] = new connectionProfile();
            cnnProfile[iConnector].setInstituttionCode(instCfg[iConnector].getValue("INSTITUTION", "INTERFACECODE"));

            cnnProfile[iConnector].setInstituttionIndex(iConnector);
            cnnProfile[iConnector].setIncommingqueue(icmQueue);
            cnnProfile[iConnector].setIsoCfg(getIsoFormatByScope(instCfg[iConnector].getValue("INSTITUTION", "SCOPE")));

            cnnProfile[iConnector].setInstituttionIndex(iConnector);
            cnnProfile[iConnector].setInstData(institutionData[iConnector]);
            cnnProfile[iConnector].setSystemGlobal(this);
            //cnnProfile[iConnector].setParentThread(hsmConnectors[iConnector]);

            cnnProfile[iConnector].init();
            cnnProfile[iConnector].start();

        }
    }

    public connectionProfile[] getConnections() {
        return cnnProfile;
    }

    public hsmConnections getHSMConnectors() {
        return hsmCnns;
    }

    public systemMessageQueue getIcmQueue() {
        return icmQueue;
    }

    public hsmSecQueue getMhsmQueue() {
        return mhsmQueue;
    }

    public secObjQueue getMsecObjQueue() {
        return msecObjQueue;
    }

    public hsmCommandQueue getHsmCmdQueue() {
        return hsmCmdQueue;
    }

    public hsmSecQueue gethsmQueue() {
        return mhsmQueue;
    }

    public systemMessageQueue getOutQueue() {
        return outQueue;
    }

    public cfgNode getInstitutionDataConfig(String pinstitutionName) {
        cfgNode rs;
        try {
            if (instCfg != null) {
                for (int i = 0; i < instCfg.length; i++) {
                    if (instCfg[i].getValue("INSTITUTION", "INTERFACECODE").equals(pinstitutionName.toUpperCase())) {
                        return instCfg[i].getXmlNode("INSTITUTION");
                    }
                }
            }
        } catch (Exception ex) {
        }
        return null;
    }

    public connectionProfile getConnectorByInstitution(String pinstitutionName) {
        try {
            if (cnnProfile != null) {
                for (int i = 0; i < instCfg.length; i++) {
                    if (cnnProfile[i].getInstituttionCode().toUpperCase().equals(pinstitutionName)) {
                        return cnnProfile[i];
                    }
                }
            }
        } catch (Exception ex) {
        }
        return null;
    }

    public iqueryactionable getInstitutionData(String pinstitutionName) {
        try {
            if (institutionData != null) {
                for (int i = 0; i < institutionData.length; i++) {
                    if (institutionData[i].getInsCode().toUpperCase().equals(pinstitutionName)) {
                        return institutionData[i];
                    }
                }
            }
        } catch (Exception ex) {
        }
        return null;
    }

    public cfgNode getIsoConfigByInstition(String pScope) {
        try {
            if (isoFormatLodaer != null) {
                List<cfgNode> allIsoFMT = isoFormatLodaer.getNodeInScope(nodeType.HEADER, pScope);
                if (allIsoFMT.size() > 0) {
                    return allIsoFMT.get(0);
                }

            }
        } catch (Exception ex) {
        }
        return null;
    }

    public cfgParser getSystemConfig() {
        return systemCfg;
    }

    public cfgParser getcfgRouting() {
        return routingCfg;
    }

    public void restartInstConnection(int pconnectorIndex) {
        try {
            cnnProfile[pconnectorIndex].refresh();
        } catch (Exception ex) {
        }

    }

    private boolean isStartThread(Thread[] pThreads) {
        boolean rs = true;
        for (int i = 0; i < pThreads.length; i++) {
            if (!((pThreads[i].getState() != Thread.State.RUNNABLE) || (pThreads[i].getState() != Thread.State.TIMED_WAITING))) {
                return false;
            }
        }
        return rs;
    }

    public int getSystemTypeByInstitution(String pinstitution) {
        return getInstitutionData(pinstitution).getInstConfig().getIntValue("TYPE");
    }

    public systemMessageSecurityQueue getSecurityQueue() {
        return securityQueue;
    }

    public systemMessageFlowControl getFlowControlQueue() {
        return flowControlQueue;
    }

    public networkConfigQueue getConfigQueue() {
        return cfgQueue;
    }

    public void reloadCFG(String pCFGPart) {
        //pCFGPart=SYSTEM, MONITORS, KFTC, ITMX...
        if (pCFGPart.toUpperCase().equals("SYSTEM")) {
            systemCfg.reLoadCFG();
            return;
        }

        //getInstitutionDataConfig(pCFGPart).reLoadCFG();
    }

    public issSettings getIssCfg() {
        return issCfg;
    }

    public void setIssCfg(issSettings issCfg) {
        this.issCfg = issCfg;
    }

    public messagePatternLoader getPatternObj() {
        return patternLoader;
    }

    public messagePatternLoader getIssCardData() {
        return issCardData;
    }

    public messagePatternLoader getIssResformat() {
        return issResformat;
    }

    public iIssProcessing getIssResponse(String pInstCode) {
        for (int i = 0; i < issResponse.length; i++) {
            if (issResponse[i].getInstScope().toUpperCase().equals(pInstCode.toUpperCase())) {
                return issResponse[i];
            }
        }
        return null;
    }

    public JTextArea getTaLogs() {
        return taLogs;
    }

    public void setTaLogs(JTextArea taLogs) {
        this.taLogs = taLogs;
    }

    public SimParas getsParas() {
        return sParas;
    }

    public void setsParas(SimParas sParas) {
        this.sParas = sParas;
    }

    public void addLogs(String newLogInfo) {
        this.taLogs.setText(this.taLogs.getText() + "\n\r" + newLogInfo);
        this.taLogs.setCaretPosition(this.taLogs.getText().length());
    }

    public routingEnum getRoutingType(String pInst) {
        try {
            return routingEnum.valueOf(getInstitutionDataConfig(pInst).getValue("ROUTING"));

        } catch (Exception ex) {

        }
        return routingEnum.PORT;
    }

    public seqManager getSequencyService() {
        return sequencyService;
    }

    public dataDictionary<IsoMessage> getReversalMap() {
        return reversalMap;
    }

    public dataDictionary<IsoMessage> getOriginalMap() {
        return originalMap;
    }

    public delayTube getDelayQueue() {
        return delayQueue;
    }

    public void setDelayQueue(delayTube delayQueue) {
        this.delayQueue = delayQueue;
    }
    
    
    
    
}
