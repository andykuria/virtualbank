/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iss;

/**
 *
 * @author netone
 */
public class SimParas {

    private boolean isShowSimpleLogs=true;
    private boolean isShowCnns=false;
    private boolean isShowDetails=false;
    private boolean isSendReversal=false;
    private int sendRevTime=0;

    public boolean isIsShowSimpleLogs() {
        return isShowSimpleLogs;
    }

    public void setIsShowSimpleLogs(boolean isShowSimpleLogs) {
        this.isShowSimpleLogs = isShowSimpleLogs;
    }

    public boolean isIsShowCnns() {
        return isShowCnns;
    }

    public void setIsShowCnns(boolean isShowCnns) {
        this.isShowCnns = isShowCnns;
    }

    public boolean isIsShowDetails() {
        return isShowDetails;
    }

    public void setIsShowDetails(boolean isShowDetails) {
        this.isShowDetails = isShowDetails;
    }

    public boolean isIsSendReversal() {
        return isSendReversal;
    }

    public void setIsSendReversal(boolean isSendReversal) {
        this.isSendReversal = isSendReversal;
    }

    public int getSendRevTime() {
        return sendRevTime;
    }

    public void setSendRevTime(int sendRevTime) {
        this.sendRevTime = sendRevTime;
    }
    
    
}
