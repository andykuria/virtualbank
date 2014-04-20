/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package hsm;

import lib.CommonLib;

/**
 *
 * @author netone
 */
public class pinInfo {
    private String pinText;
    private String interfaceCode;
    private String pinBlockInHex;
    private Integer hsmID;

    
    public pinInfo() {
        pinText="";
        interfaceCode="";
        pinBlockInHex="";
        hsmID=0;
    }

    public String getPinText() {
        return pinText;
    }

    public void setPinText(String pinText) {
        this.pinText = pinText;
    }

    public String getInterfaceCode() {
        return interfaceCode;
    }

    public void setInterfaceCode(String interfaceCode) {
        this.interfaceCode = interfaceCode;
    }

    public String getPinBlockInHex() {
        return pinBlockInHex;
    }
    
    public byte[] getPinBlockInBytes() {
        return CommonLib.hex2Byte(getPinBlockInHex());
    }

    public void setPinBlockInHex(String pinBlockInHex) {
        this.pinBlockInHex = pinBlockInHex;
    }

    public Integer getHsmID() {
        return hsmID;
    }

    public void setHsmID(Integer hsmID) {
        this.hsmID = hsmID;
    }
    
    public boolean isPinBlock()
    {
        return pinBlockInHex.length()>0;
    }
    
    @Override
    public int hashCode()
    {
    //    String tmpStringHash=pinText+interfaceCode;
        return hsmID;
    }
    
    
}
