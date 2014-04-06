/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package lib;

import java.util.Arrays;

/**
 *
 * @author netone
 */
public class secObjInfo {
    msgSecurityEnum typeOfSec=msgSecurityEnum.READY;
    String[] fields;
    String hsmCommnadID="0001";
    int msgID=0;
    String sZone="";
    String dZone="";

    public secObjInfo() {
        fields=new String[0];
    }

    public secObjInfo(msgSecurityEnum typeOfSec) {
        fields=new String[0];
        this.typeOfSec = typeOfSec;
    }

    public secObjInfo(secObjInfo aclone) {
        this.typeOfSec = aclone.getTypeOfSec();
        this.hsmCommnadID=aclone.getHsmCommnadID();
        this.fields= Arrays.copyOf(aclone.fields, aclone.fields.length);
        this.msgID=aclone.getMsgID();
        this.sZone=aclone.getsZone();
        this.dZone=aclone.getdZone();
    }

    
    public msgSecurityEnum getTypeOfSec() {
        return typeOfSec;
    }

    public void setTypeOfSec(msgSecurityEnum typeOfSec) {
        this.typeOfSec = typeOfSec;
    }

    public String[] getFields() {
        return fields;
    }

    public void setFields(String[] fields) {
        this.fields = fields;
    }

    public String  getHsmCommnadID() {
        return hsmCommnadID;
    }

    public void setHsmCommnadID(String hsmCommnadID) {
        this.hsmCommnadID = hsmCommnadID;
    }

    public int getMsgID() {
        return msgID;
    }

    public void setMsgID(int msgID) {
        this.msgID = msgID;
    }

    public String getsZone() {
        return sZone;
    }

    public void setsZone(String sZone) {
        this.sZone = sZone;
    }

    public String getdZone() {
        return dZone;
    }

    public void setdZone(String dZone) {
        this.dZone = dZone;
    }
    
    
    
    
}
