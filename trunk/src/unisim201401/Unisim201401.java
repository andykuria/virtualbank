/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package unisim201401;

import cfg.cfgParser;
import processing.hsmCmdProcess;
import processing.hsm_queueProcess;
import processing.systemMessageFlowControlProcess;
import processing.systemMessageProcessing;
import processing.systemMessageSender;

/**
 *
 * @author netone
 */
public class Unisim201401 {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here

        systemLoader systemParas = new systemLoader();
        systemParas.initSystemConfig();
        MainForm frmsimulator = new MainForm();
        frmsimulator.setTitle(systemParas.getSystemConfig().getValue("GLOBAL", "SHOWNAME"));
        frmsimulator.setSystemData(systemParas);
        frmsimulator.pack();
        frmsimulator.setVisible(true);


        systemMessageProcessing msgPreProccess = new systemMessageProcessing();
        msgPreProccess.setSystemGlobalInfo(systemParas);
        msgPreProccess.setMsgFlowControlQueue(systemParas.getFlowControlQueue());

        msgPreProccess.setInstitutionQueue(systemParas.getIcmQueue());

        msgPreProccess.start();


        //systemOutgoingQueue ogiQueue = new systemOutgoingQueue(syscon.getOutQueue());

        systemMessageFlowControlProcess flowProcess = new systemMessageFlowControlProcess(systemParas.getFlowControlQueue());
        flowProcess.setSystemGlobalInfo(systemParas);
        flowProcess.sethsmqueue(systemParas.gethsmQueue());
        flowProcess.setSystemControlQueue(systemParas.getFlowControlQueue());
        flowProcess.setInstitutionQueue(systemParas.getOutQueue());
        flowProcess.start();


        hsm_queueProcess hsmObjProcess = new hsm_queueProcess();
        hsmObjProcess.setSystemGlobalInfo(systemParas);
        hsmObjProcess.setmObjSecQueue(systemParas.getMsecObjQueue());
        hsmObjProcess.setSecurityQueue(systemParas.getSecurityQueue());
        hsmObjProcess.setMhsmqueue(systemParas.getMhsmQueue());
        hsmObjProcess.setCmdHsmQueue(systemParas.getHsmCmdQueue());
        hsmObjProcess.start();



        


        systemMessageSender messageSender = new systemMessageSender();
        messageSender.setSystemGlobalInfo(systemParas);
        messageSender.setInstitutionQueue(systemParas.getOutQueue());

        messageSender.start();



        systemParas.startHsmConnector(100);
        systemParas.startInstConnector(messageSender);

        hsmCmdProcess cmdHsmSender = new hsmCmdProcess();
        cmdHsmSender.setCmdQueue(systemParas.getHsmCmdQueue());
        cmdHsmSender.setHsmCnns(systemParas.getHSMConnectors());
        cmdHsmSender.setSystemGlobal(systemParas);
        cmdHsmSender.start();


    }
}
