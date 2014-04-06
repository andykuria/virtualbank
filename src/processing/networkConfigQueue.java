/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package processing;

import iss.showLogEnum;
import java.util.concurrent.ConcurrentHashMap;
import lib.CommonLib;
import lib.instBOX;

/**
 *
 * @author kt1
 */
public class networkConfigQueue {
    ConcurrentHashMap<Integer, instBOX> msgConfigQueue;
    public networkConfigQueue() {
        msgConfigQueue = new ConcurrentHashMap<Integer, instBOX>();
    }

    public synchronized void addMessage(Integer pID, instBOX pMsg) {
        if (msgConfigQueue != null) {
            msgConfigQueue.put(pID, pMsg);
        }
    }

    public synchronized void removeMessage(Integer pSeqID)
    {
        try
        {
            msgConfigQueue.remove(pSeqID);
            CommonLib.PrintScreen(null,"SMSQ: remove Message Seq ID = "+pSeqID,showLogEnum.DEFAULT);
            
        }
        catch (Exception ex)
        {
            CommonLib.PrintScreen(null,"ERR SMSQ: cannot remove Message Seq ID = "+pSeqID,showLogEnum.DEFAULT);
        }
            
            
    }
    
    public synchronized instBOX checkMessage(Integer pSeqID) {
        return msgConfigQueue.get(pSeqID);
    }
    
    public synchronized instBOX peekMessage(Integer pSeqID) {
        instBOX pRs= msgConfigQueue.get(pSeqID);
        if (pRs!=null) msgConfigQueue.remove(pSeqID);
        return pRs;
    }
    
    public synchronized  int size()
    {
        try
        {
            return msgConfigQueue.size();
        }
        catch(Exception ex)
        {
            return 0;
        }
    }
}
