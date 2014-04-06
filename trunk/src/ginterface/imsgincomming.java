/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ginterface;

import iso8583.IsoMessage;

/**
 *
 * @author minhdbh
 */
public interface imsgincomming {
    public void enqueueMessage(IsoMessage pisomsg);
    public IsoMessage peekMessage();
    public String getInsCode();
    public void setInsCode(String pinsCode);

}
