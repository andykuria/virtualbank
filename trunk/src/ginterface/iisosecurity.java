/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ginterface;

import lib.msgSecurityEnum;

/**
 *
 * @author minhdbh
 */
public interface iisosecurity {
    public void setMsgSecType(msgSecurityEnum pmsgType);
    public msgSecurityEnum getMsgSecType();
    public int getHsmID();
    public void setHsmID(int phsmID);
    public String getHSMReturnValue();
}
