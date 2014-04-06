/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package iso8583;

import ginterface.iisosecurity;
import java.util.Date;
import lib.DateUtils;
import lib.msgSecurityEnum;

/**
 *
 * @author minhdbh
 */
public class msgSecurity implements iisosecurity{
    private msgSecurityEnum msgType;
    private int hsmCommandID;
    private String hsmReturnValue;
    private String keyCheckValue;
    private String hsmErrCode;
    private int commandLen;
    private int msgHeaderLen;
    private int institutionIndex;
    private String institutionCode;
    private Date createdDate;
    
    public msgSecurity()
    {
        msgType=msgSecurityEnum.READY;
        hsmCommandID=0;
        hsmReturnValue="";
        keyCheckValue="";
        hsmErrCode = "";
        commandLen = 0;
        msgHeaderLen = 4;
        institutionIndex=-1;
        institutionCode="";
        createdDate=DateUtils.getDate();
    }

    public msgSecurity(msgSecurityEnum pmsgType)
    {
        msgType=pmsgType;
        hsmCommandID=0;
        hsmReturnValue="";
        keyCheckValue="";
        hsmErrCode = "";
        commandLen = 0;
        msgHeaderLen = 4;
        institutionIndex=-1;
        institutionCode="";
        createdDate=DateUtils.getDate();
    }

    public msgSecurity(msgSecurity pmsgSecurity){
        msgType = pmsgSecurity.getMsgSecType();
        hsmCommandID = pmsgSecurity.getHsmID();
        hsmReturnValue=pmsgSecurity.getHSMReturnValue();
        keyCheckValue=pmsgSecurity.getKeyCheckValue();
        hsmErrCode = pmsgSecurity.getHsmErrCode();
        commandLen = pmsgSecurity.getCommandLen();
        msgHeaderLen = pmsgSecurity.getMsgHeaderLen();
        institutionIndex=pmsgSecurity.getInstitutionIndex();
        institutionCode=pmsgSecurity.getInstitutionCode();
        createdDate=pmsgSecurity.getCreatedDate();
    }

    public void setHsmErrCode(String phsmErrCode) {
        this.hsmErrCode = phsmErrCode;
    }

    public String getHsmErrCode() {
        return hsmErrCode;
    }

    public String getKeyCheckValue() {
        return keyCheckValue;
    }

    public void setKeyCheckValue(String keyCheckValue) {
        this.keyCheckValue = keyCheckValue;
    }

    public void setMsgSecType(msgSecurityEnum pmsgType)
    {
        msgType=pmsgType;
    }

    public msgSecurityEnum getMsgSecType() {
        return msgType;
    }

    public int getHsmID() {
        return hsmCommandID;
    }

    public void setHsmID(int phsmID) {
        hsmCommandID=phsmID;
    }

    public void setHsmReturnValue(String hsmReturnValue) {
        this.hsmReturnValue = hsmReturnValue;
    }

    public String getHSMReturnValue() {
        return hsmReturnValue;
    }

    public int getCommandLen() {
        return commandLen;
    }

    public void setCommandLen(int commandLen) {
        this.commandLen = commandLen;
    }

    public int getMsgHeaderLen() {
        return msgHeaderLen;
    }

    public void setMsgHeaderLen(int msgHeaderLen) {
        this.msgHeaderLen = msgHeaderLen;
    }

    public String getInstitutionCode() {
        return institutionCode;
    }

    public int getInstitutionIndex() {
        return institutionIndex;
    }

    public void setInstitutionCode(String pinstitutionCode) {
        this.institutionCode = pinstitutionCode;
    }

    public void setInstitutionIndex(int pinstitutionIndex) {
        this.institutionIndex = pinstitutionIndex;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public String getDetails()
    {
        String rs="";
        rs="Type: %s - INST: %s - Value: %s";
        rs=String.format(rs, String.valueOf(msgType),institutionCode,hsmReturnValue);
        return rs;
    }



}
