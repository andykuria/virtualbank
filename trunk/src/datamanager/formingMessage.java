/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package datamanager;

import cfg.cfgNode;
import cfg.cfgParser;
import iso8583.IsoMessage;

/**
 *
 * @author netone
 */
public class formingMessage {
    cfgParser tmpData;

    public formingMessage(cfgParser tmpData) {
        this.tmpData = tmpData;
    }
    
    public IsoMessage getMessage(String msgPatternID, String msgPan)
    {
        IsoMessage messageNeedToGen=new IsoMessage();
        messageNeedToGen.setMessageState(false);
        switch (tmpData.getXmlType())
        {
            case PATTRENDATA:
                cfgNode messagePattern=tmpData.getXmlNode(msgPatternID);
                
                break;
        }
        return messageNeedToGen;
                
    }
}
