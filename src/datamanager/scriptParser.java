/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package datamanager;

import globalutils.scriptEnum;
import java.util.HashMap;
import java.util.Map;
import lib.CommonLib;

/**
 *
 * @author netone
 */
public class scriptParser {
    scriptEnum typeOfScript;
    int seqID=0;
    String msgId="";    
    int delayTime;
    Map<String,String> allInfo;

    public scriptParser() {
        allInfo=new HashMap<>();
        typeOfScript=scriptEnum.UNKNOWN;
        delayTime=10;
    }

    public scriptParser(String scriptPattern) {
        allInfo=new HashMap<>();
        typeOfScript=scriptEnum.UNKNOWN;
        delayTime=10;
        parseScript(scriptPattern); 
    }
    public int getSeqID() {
        return seqID;
    }

    public void setSeqID(int seqID) {
        this.seqID = seqID;
    }
    
    
    public String getValue(String pkey)
    {
        return allInfo.get(pkey);
        
    }   
    
    public Map<String,String> getKeyInfo()
    {
        return allInfo;
    }

    public scriptEnum getTypeOfScript() {
        return typeOfScript;
    }

    public void setTypeOfScript(scriptEnum typeOfScript) {
        this.typeOfScript = typeOfScript;
    }

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public int getDelayTime() {
        return delayTime;
    }

    public void setDelayTime(int delayTime) {
        this.delayTime = delayTime;
    }
    
    private void parseScript(String scriptDetail)
    {
        String[] valuesField = scriptDetail.split("::");
        for(String iVal:valuesField)
        {
            String[] subFields=iVal.split("=");
            Integer direction = CommonLib.getStringCode(subFields[0].toUpperCase());
            switch (direction)
            {
                case 5: //F
                    typeOfScript=scriptEnum.FINACIAL;
                    msgId=subFields[1];
                    break;
                case 17: //R
                    typeOfScript=scriptEnum.REVERSAL;
                    msgId=subFields[1];
                    break;
                case 311://DL
                    delayTime=CommonLib.valueOf(subFields[1]);
                    break;
                case 150013://PAN
                    allInfo.put("PAN", subFields[1]);
                    break;
                case 1417: //OR
                    allInfo.put("OR", subFields[1]);
                    break;
            }
            
            
        }
    }
    
}
