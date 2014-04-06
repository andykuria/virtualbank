/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ginterface;

import globalutils.KeyExhType;
import iso8583.IsoMessage;
import iso8583.msgSecurity;
import processing.networkConfigQueue;


/**
 *
 * @author minhdbh
 */
public interface imnetworkmessage {

    public IsoMessage genSignonMessage(int pIndexPort);

    public IsoMessage genSignoffMessage(int pIndexPort);

    public IsoMessage genEchoMessage(int pIndexPort);

    public IsoMessage genKeyEchMessage(KeyExhType pkeyEchType,int pIndexPort);

    public IsoMessage genNewKeyMessage(KeyExhType pkeyEchType,int pIndexPort);

    public IsoMessage genCutoverMessage(int pIndexPort);

    public IsoMessage getForwardMsg(IsoMessage pmsg);

    public IsoMessage[] processNetwork(IsoMessage pmsg);

    public IsoMessage[] processReversal(IsoMessage pmsg);

    public void setConfiguration(iqueryactionable pconfiguration);

    public KeyExhType getKeyExhType(String pF53Value);

    public IsoMessage getResponse(IsoMessage preqMsg);

    public IsoMessage getResponse(IsoMessage preqMsg, msgSecurity pSec);

    public IsoMessage changeNetworkConfig(IsoMessage pmsg);

    public IsoMessage saveNetworkCfg(IsoMessage pmsg);

    public void setCfgNetwork(networkConfigQueue pcfgNetwork);
}
