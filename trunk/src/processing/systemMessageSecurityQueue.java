/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package processing;

import iso8583.IsoMessage;
import iso8583.msgSecurity;
import iss.showLogEnum;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import lib.CommonLib;
import lib.secObjInfo;



/**
 *
 * @author kt1
 */
public class systemMessageSecurityQueue {

    ConcurrentHashMap<Integer, IsoMessage> msgSecurityQueue;

    public systemMessageSecurityQueue() {
        msgSecurityQueue = new ConcurrentHashMap<Integer, IsoMessage>();
    }

    public synchronized void addMessage(IsoMessage pMsg) {
        if (msgSecurityQueue != null) {
            msgSecurityQueue.put(pMsg.getSeqID(), pMsg);
            CommonLib.PrintScreen(null, "SMSQ add: "+pMsg.getTraceInfo(),showLogEnum.DEFAULT);
        }
    }

    public synchronized void removeMessage(Integer pSeqID)
    {
        try
        {
            msgSecurityQueue.remove(pSeqID);
            CommonLib.PrintScreen(null, "SMSQ: remove Message Seq ID = {"+pSeqID+"}",showLogEnum.DEFAULT);
        }
        catch (Exception ex)
        {
            CommonLib.PrintScreen( null,"ERR SMSQ: cannot remove Message Seq ID = {"+pSeqID+"}",showLogEnum.DEFAULT);
        }
            
            
    }
    
    public synchronized IsoMessage checkMessage(secObjInfo pSec) {
        
        
        return msgSecurityQueue.get(pSec.getMsgID());
        
    }
    
  
    
    public synchronized  int size()
    {
        try
        {
            return msgSecurityQueue.size();
        }
        catch(Exception ex)
        {
            return 0;
        }
    }
    
}
