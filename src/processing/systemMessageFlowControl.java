/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package processing;

import iso8583.IsoMessage;
import iss.showLogEnum;
import java.util.concurrent.ConcurrentLinkedQueue;
import lib.CommonLib;

/**
 *
 * @author kt1
 */
public class systemMessageFlowControl {
    ConcurrentLinkedQueue<IsoMessage> msgQueue;
    
    public systemMessageFlowControl()
    {
        msgQueue=new ConcurrentLinkedQueue<IsoMessage>();
    }
    
    public synchronized void enqueueMessage(IsoMessage newMsg)
    {
        if (msgQueue!=null) {
            msgQueue.add(newMsg);
            CommonLib.PrintScreen(null,"SMFC add: "+newMsg.getTraceInfo(),showLogEnum.DEFAULT); 
        }
    }
    
    public synchronized IsoMessage peekMessage()
    {
        if(msgQueue!=null)
        {
            return msgQueue.poll();
        }
        return null;
    }
    
    public synchronized int size()
    {
        if(msgQueue!=null)
        {
            return msgQueue.size();
        }
        return 0;
    }
    
}
