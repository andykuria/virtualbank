/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package processing;

import iso8583.IsoMessage;
import java.util.concurrent.ConcurrentLinkedQueue;
import lib.CommonLib;

/**
 *
 * @author kt1
 */
public class hsmSecQueue {

    ConcurrentLinkedQueue<IsoMessage> mconverterqueue;

    public hsmSecQueue() {
        mconverterqueue = new ConcurrentLinkedQueue<IsoMessage>();
    }

    public synchronized void enqueue(IsoMessage pisomsg) {
        mconverterqueue.add(pisomsg);
        
    }

    public synchronized IsoMessage peekMessage() {
        IsoMessage tmpRs = mconverterqueue.poll();
        return tmpRs;
    }

    public synchronized int size() {
        try {
            return mconverterqueue.size();
        } catch (Exception ex) {
            return 0;
        }
    }
}
