/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lib;

/**
 *
 * @author kt1
 */
public class instBOX {
    msgSecurityEnum boxType;
    String boxTAG;
    Integer boxID;
    String boxValue;
    String instCode;
    
    public instBOX()
    {
        boxType= msgSecurityEnum.READY;
        boxTAG="";
        boxID=0;
        boxValue="";
        instCode="";
    }
    
    public instBOX(Integer pID, msgSecurityEnum pType, String pTag, String pVal, String pinstCode)
    {
        boxID=pID;
        boxType=pType;
        boxTAG=pTag;
        boxValue=pVal;
        instCode=pinstCode;
    }

    public void setInstCode(String pinstCode) {
        this.instCode = pinstCode;
    }

    public String getInstCode() {
        return instCode;
    }

    public Integer getBoxID() {
        return boxID;
    }

    public String getBoxTAG() {
        return boxTAG;
    }

    public msgSecurityEnum getBoxType() {
        return boxType;
    }

    public String getBoxValue() {
        return boxValue;
    }

    public void setBoxID(Integer pboxID) {
        this.boxID = pboxID;
    }

    public void setBoxTAG(String pboxTAG) {
        this.boxTAG = pboxTAG;
    }

    public void setBoxType(msgSecurityEnum pboxType) {
        this.boxType = pboxType;
    }

    public void setBoxValue(String pboxValue) {
        this.boxValue = pboxValue;
    }
    
    public String toString()
    {
        return String.format("Msg Id: {%d}, Inst: %s, TAG: %s, Type: %s, Value %s", getBoxID(), getInstCode(),getBoxTAG(),String.valueOf(getBoxType()),getBoxValue());
    }
    
}
