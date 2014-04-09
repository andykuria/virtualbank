/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package iss;

import cfg.cfgNode;
import datamanager.messagePatternLoader;
import iso8583.IsoMessage;
import unisim201401.systemLoader;

/**
 *
 * @author minhdbh
 */
public interface iIssProcessing {

    public void setInstScope(String pIns);

    public String getInstScope();

    public void setCards(messagePatternLoader cardList);

    public void setIssResponseFormat(messagePatternLoader issResponseFormat);

    public IsoMessage getResponse(IsoMessage requestMsg);

    public IsoMessage makeRevFromFin(IsoMessage requestMsg, cfgNode revFmt);

    public void setSystemGlobalInfo(systemLoader systemGlobalInfo);
}
