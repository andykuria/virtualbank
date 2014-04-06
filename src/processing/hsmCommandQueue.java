/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package processing;

import hsm.hsmCmdObj;
import iso8583.IsoMessage;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 *
 * @author minhdbh
 */
public class hsmCommandQueue {
    ConcurrentLinkedQueue<hsmCmdObj> hsmCmdQueue;

    public hsmCommandQueue() {
        hsmCmdQueue=new ConcurrentLinkedQueue<>();
    }
    
    public void addNewCmd(hsmCmdObj hsmCommand)
    {
        hsmCmdQueue.add(hsmCommand);
    }
    
    public hsmCmdObj peekCmd()
    {
        return hsmCmdQueue.poll();
    }
    
    
}
