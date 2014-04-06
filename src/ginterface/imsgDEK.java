/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ginterface;

import iso8583.IsoMessage;
import iso8583.msgSecurity;

/**
 *
 * @author kt1
 */
public interface imsgDEK {
    public void enqueueMessage(IsoMessage pisomsg);
    public IsoMessage peekMessage();
    //public void removeMSG(IsoMessage rmvMsg);
    public IsoMessage[] getDEKrequire();
    public void updateDEK(IsoMessage pDEKMsg);
    
    public IsoMessage getDEKOK(msgSecurity pHSMResult,String pInstCode);

}
