/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ginterface;

import datamanager.messagePatternLoader;
import globalutils.KeyExhType;
import iso8583.IsoMessage;
import iso8583.msgSecurity;
import processing.networkConfigQueue;

/**
 *
 * @author minhdbh
 */
public interface iresponsemessage {

    public IsoMessage makeResponseMsg(IsoMessage pmsg, messagePatternLoader msgPattern);

    public IsoMessage makeNetworkResponseMsg(IsoMessage pmsg, messagePatternLoader msgPattern);

    public void setConfiguration(iqueryactionable pconfiguration);

    public IsoMessage changeNetworkConfig(IsoMessage pmsg);

    public IsoMessage saveNetworkCfg(IsoMessage pmsg);

    public void setCfgNetwork(networkConfigQueue pcfgNetwork);
    
}
