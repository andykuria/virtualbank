/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ginterface;

import exceptionshandle.bicsexception;
import iso8583.IsoMessage;
import iso8583.msgSecurity;
import iso8583.msgSecurityCommand;
import java.util.Queue;
import lib.messageContraintsEnum;
import lib.msgSecurityEnum;

import unisim201401.systemLoader;

/**
 *
 * @author minhdbh
 */
public interface iprocessingmsg {
    public messageContraintsEnum checkMessageState(IsoMessage pmsg);
    public IsoMessage convertToExternalFormat(IsoMessage pmsg)  throws bicsexception;
    public void processNetwork(IsoMessage pmsg);
    public Queue<msgSecurityEnum> getSecurityQueue(IsoMessage pmsg);
    public Queue<msgSecurityEnum> getSecurityQueueResponseFinacial(IsoMessage pmsg);
    //public IsoMessage convertToInternalFormat(IsoMessage pmsg);

    //public msgSecurityCommand getSecurityCommand(msgSecurityEnum pSecType);
    public msgSecurityCommand getSecurityCommand(IsoMessage pmsg, msgSecurityEnum pSecType);
    //public IsoMessage updateSecuirty(IsoMessage pmsg, msgSecurityEnum pSecType);
    public IsoMessage updateSecuirty(IsoMessage pmsg, msgSecurity pSec);
    
    public void setConfiguration(iqueryactionable pconfiguration);
    public void setSystemGlobalInfo(systemLoader psystemGlobalInfo);

}
