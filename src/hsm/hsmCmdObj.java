/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hsm;

import lib.msgSecurityEnum;

/**
 *
 * @author minhdbh
 */
public class hsmCmdObj {

    private msgSecurityEnum msgType;
    private int hsmCommandID;
    private String commandHSM="";

    public hsmCmdObj()
    {
        msgType=msgSecurityEnum.READY;
        hsmCommandID=0;
        commandHSM="";
    }

    public void setCommandHSM(String pcommandHSM) {
        this.commandHSM = pcommandHSM;
    }

    public void setHsmCommandID(int phsmCommandID) {
        this.hsmCommandID = phsmCommandID;
    }

    public void setMsgType(msgSecurityEnum pmsgType) {
        this.msgType = pmsgType;
    }

    public String getCommandHSM() {
        return commandHSM;
    }

    public int getHsmCommandID() {
        return hsmCommandID;
    }

    public msgSecurityEnum getMsgType() {
        return msgType;
    }

    public String toString()
    {
        return "HSM Command: " + String.valueOf(msgType) + " - (ID) " + String.valueOf(hsmCommandID) + " - " + commandHSM;
    }

    
    
}
