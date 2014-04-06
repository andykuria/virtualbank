/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package hsm;

import iso8583.msgSecurity;
import java.util.Iterator;

import java.util.concurrent.ConcurrentLinkedQueue;


/**
 *
 * @author minhdbh
 */
public class hsmQueue {
    ConcurrentLinkedQueue< msgSecurity> securityQueue=new ConcurrentLinkedQueue< msgSecurity>();

    public hsmQueue()
    {

    }

    public void enqueueNewSecurity(msgSecurity pnewObj)
    {
        securityQueue.add(pnewObj);
    }

    /*public void removeMsgSec(Integer msgSecID)
    {
        securityQueue.remove(msgSecID);
    }*/

    /*public Integer[] peekSecurity()
    {
        Integer[] keyArray=new Integer[securityQueue.size()];
        Iterator<Integer> itrKey= securityQueue.keySet().iterator();
        int iKey=0;
        while (itrKey.hasNext())
        {
            keyArray[iKey]= itrKey.next();
            iKey++;
        }
        return keyArray;
    }*/

    public msgSecurity peekSecurity()
    {
        return securityQueue.poll();
    }

    public int size()
    {
        return securityQueue.size();
    }
    

}
