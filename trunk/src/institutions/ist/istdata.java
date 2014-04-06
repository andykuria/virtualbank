/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package institutions.ist;


import cfg.cfgNode;
import ginterface.iqueryactionable;
import globalutils.ConfigInfo;
import globalutils.ConnectionMode;
import globalutils.LineModeEnum;
import iso8583.ConfigIsoMessage;
import iso8583.HeaderProcessing;
import iso8583.IsoHeaderType;


/**
 *
 * @author minhdbh
 */
public final class istdata implements iqueryactionable{

    private cfgNode instConfig;

   private HeaderProcessing hp;

    
    
    public cfgNode getInstConfig() {
        return instConfig; 
    }

    public void setInstConfig(cfgNode pinstConfig) {
        this.instConfig = pinstConfig;
    }



    public istdata()
    {
        hp=null;
    }

    public HeaderProcessing getHeaderInfo()
    {
       IsoHeaderType ht=IsoHeaderType.ASCII;
       if (getInstConfig().getValue("DATAMODE").toUpperCase().equals("ASCII")) ht=IsoHeaderType.ASCII;
       if (getInstConfig().getValue("DATAMODE").toUpperCase().equals("BYTE")) ht=IsoHeaderType.BYTE;
       if (getInstConfig().getValue("DATAMODE").toUpperCase().equals("SEQ_ITMX")) ht=IsoHeaderType.SEQ_ITMX;
       hp = new HeaderProcessing(getInstConfig().getIntValue( "HEADERLENGTH"));
       hp.setHeaderType(ht);
       return hp;

    }

    public LineModeEnum getLineMode() {
        return LineModeEnum.valueOf(instConfig.getValue("LINEMODE"));
    }

    public ConnectionMode getConnMode() {
        return ConnectionMode.valueOf(instConfig.getValue("MODE"));
    }

    public String getInsCode()
    {
        return getInstConfig().getValue("INTERFACECODE");
    }
    
    public String getInsScope()
    {
        return getInstConfig().getValue("SCOPE");
    }

}
