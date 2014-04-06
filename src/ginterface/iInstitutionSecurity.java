/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ginterface;

import hsm.hsmCmdObj;
import iso8583.IsoMessage;
import iso8583.msgSecurity;
import java.util.Queue;
import lib.secObjInfo;
import unisim201401.systemLoader;

/**
 *
 * @author netone
 */
public interface iInstitutionSecurity {

    public hsmCmdObj getSecCommand(IsoMessage pmsg, secObjInfo pSecType);

    public String getZone();

    public void setZone(String zone);

    public void setSystemGlobalInfo(systemLoader systemGlobalInfo);

    public IsoMessage updateSecurity(IsoMessage pmsg, msgSecurity pSec);

    public Queue<secObjInfo> getSecurityList(IsoMessage pmsg);
}
