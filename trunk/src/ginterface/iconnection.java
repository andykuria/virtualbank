/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ginterface;

import cfg.cfgNode;
import iso8583.ConfigIsoMessage;
import iso8583.HeaderProcessing;

/**
 *
 * @author kt1
 */
public interface iconnection {
    public void start();
    public void init();
    public void sendData(byte[] value, int pIndexPort);
    public void setIsoCfg(cfgNode pisoCfg);
    public void setInstituttionIndex(int pinstituttionIndex) ;
    public int getInstituttionIndex();



    public void setInstituttionCode(String pinstituttionCode);
    public String getInstituttionCode();
    public void stopConnection() ;
    public void startConnection() ;
    
    public String getSystemStateDetail() ;
    public boolean checkStatus() ;
    public void clearState(String key) ;
    public void addSystemState(String key, String value);

    
}
