/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package institutions.ist;

import datamanager.messagePatternLoader;
import ginterface.imnetworkmessage;

import ginterface.iqueryactionable;
import ginterface.iresponsemessage;
import globalutils.KeyExhType;
import iso8583.IsoMessage;


import iso8583.IsoMessageType;
import iso8583.msgSecurity;

import lib.CommonLib;
import lib.DateUtils;
import lib.instBOX;
import lib.msgSecurityEnum;
import processing.networkConfigQueue;

/**
 *
 * @author minhdbh
 */
public class istmsg implements iresponsemessage {

    private istdata istConfiguration;
    private networkConfigQueue cfgNetwork;

    public void setCfgNetwork(networkConfigQueue pcfgNetwork) {
        this.cfgNetwork = pcfgNetwork;
    }

    public void setConfiguration(iqueryactionable pconfiguration) {
        this.istConfiguration = (istdata) pconfiguration;
    }

    @Override
    public IsoMessage makeResponseMsg(IsoMessage pmsg, messagePatternLoader msgPattern) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public IsoMessage makeNetworkResponseMsg(IsoMessage pmsg, messagePatternLoader msgPattern) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public IsoMessage changeNetworkConfig(IsoMessage pmsg) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public IsoMessage saveNetworkCfg(IsoMessage pmsg) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

  
}
