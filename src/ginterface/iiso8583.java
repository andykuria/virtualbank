/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ginterface;

import exceptionshandle.bicsexception;
import iso8583.HeaderProcessing;
import java.util.Date;
import java.util.Queue;
import lib.secObjInfo;

/**
 *
 * @author minhdbh
 */
public interface iiso8583 {

    public String[] toArray();

    public boolean isMessage();

    public String[] getIsoFields();

    public void remove(int index);

    public String getField(int index);

    public void setField(int index, String value);

    public String getStrIsoFields();

    public String getMessageID();

    public String printedMessage();

    public void initMessage(byte[] value) throws bicsexception;

    public HeaderProcessing getMsgHeader();

    public Date getReceiveDatetime();

    public void setDate(Date value);

    public String getSourceInterfaceCode();

    public void setSourceInterfaceCode(String pSourceInterfaceCode);

    public String getDesInterfaceCode();

    public void setDesInterfaceCode(String pDesInterfaceCode);

    public void addSecRequest(secObjInfo psecreq);

    public void setSecRequest(Queue<secObjInfo> psecQueueReq);

    public void setSecRequest(iInstitutionSecurity imsgSec);

    public secObjInfo peekSecRequest();

    public Queue<secObjInfo> getSecurityRequestQueue();

    public int getPortIndex();

    public void setPortIndex(int pPortIndex);
}
