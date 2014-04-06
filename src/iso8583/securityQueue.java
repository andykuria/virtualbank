/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package iso8583;


import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author minhdbh
 */
public class securityQueue {
    ConcurrentHashMap<Integer, msgSecurity> msgSecQueue;

    public securityQueue()
    {
        msgSecQueue = new ConcurrentHashMap<Integer, msgSecurity>();
    }

    public void enqueNewSecurity(Integer phsmCmdId,msgSecurity pMsgSec )
    {
        msgSecQueue.put(phsmCmdId, pMsgSec);
    }

    public void enqueNewSecurity(msgSecurity pMsgSec )
    {
        msgSecQueue.put(pMsgSec.getHsmID(), pMsgSec);
    }

    public void removeCommand(Integer phsmCmdId)
    {
        msgSecQueue.remove(phsmCmdId);
    }

    public boolean isSecurityDone()
    {
        return (msgSecQueue.isEmpty());
    }

    public boolean checkHsmCmdID(int phsmid)
    {
        return msgSecQueue.containsKey(phsmid);
    }

    public void clear()
    {
        msgSecQueue.clear();
    }
    public String getQueueDetail()
    {
        if (msgSecQueue!=null)
        {

            Iterator itr=msgSecQueue.entrySet().iterator();
            String rs="";

            while (itr.hasNext())
            {
                Map.Entry me = (Entry)itr.next();
                msgSecurity msi=(msgSecurity) me.getValue();
                rs+=" "+String.valueOf(msi.getMsgSecType());

            }
            return rs;

        }
        return "";
    }

}
