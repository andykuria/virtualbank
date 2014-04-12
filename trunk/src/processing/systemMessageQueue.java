/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package processing;

import iso8583.IsoMessage;
import iss.showLogEnum;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import lib.CommonLib;

/**
 *
 * @author minhdbh
 */
public class systemMessageQueue {

    ConcurrentLinkedQueue<IsoMessage> systemqueue;

    public systemMessageQueue() {
        systemqueue = new ConcurrentLinkedQueue<IsoMessage>();
    }

    public synchronized void add(IsoMessage pisomsg) {
        systemqueue.add(pisomsg);
        CommonLib.PrintScreen(null, "SMQ - Enqueue message: " + pisomsg.getTraceInfo(), showLogEnum.DEFAULT);

    }

    public synchronized void add(List<IsoMessage> pisomsg) {
        systemqueue.addAll(pisomsg);
        CommonLib.PrintScreen(null, "SMQ - Enqueue list of message: " + pisomsg.size(), showLogEnum.DEFAULT);

    }

    public synchronized IsoMessage peekMessage() {
        IsoMessage tmpRs = systemqueue.poll();
        return tmpRs;
    }

    public synchronized int size() {
        try {
            return systemqueue.size();
        } catch (Exception ex) {
            return 0;
        }
    }

}
