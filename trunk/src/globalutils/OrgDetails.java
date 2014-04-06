/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package globalutils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 *
 * @author minhdbh
 */
public class OrgDetails {
    
    private String orgName;
    private HashMap oD;

    public OrgDetails()
    {

    }

    public OrgDetails(HashMap value, String oN)
    {
        orgName=oN.toUpperCase();
        oD=value;
    }

    public void CloneData(HashMap value)
    {
        oD=value;
    }

     /**
     * return value in HashMap object by Key
     * @return
     */
    public String getValue(String key)
    {

        try
        {
            if (oD.containsKey(key.toUpperCase()))
            {
                return oD.get(key.toUpperCase()).toString();
            }
            else
            {
                return "";
            }
        }
        catch (Exception ex)
        {
            return "";
        }
    }

        /**
     * return value in HashMap object by Key
     * @return
     */
    public int  getIntValue(String key)
    {
        
        try
        {
            if (oD.containsKey(key.toUpperCase()))
            {
                return Integer.parseInt(oD.get(key.toUpperCase()).toString());
            }
            else
            {
                return 0;
            }
        }
        catch (Exception ex)
        {
            return  0;
        }
    }

    public String getName()
    {
        return orgName.toUpperCase();
    }

    public HashMap getThis()
    {
        return oD;
    }

    public void setValue(String key,String value)
    {
        if (oD.containsKey(key.toUpperCase()))
        {
            oD.remove(key.toUpperCase());
        }
        oD.put(key.toUpperCase(), value);
    }
   
    public ConfigType[] toArray()
    {
        ConfigType[] ctrs=new  ConfigType[oD.size()];
        Iterator itr = oD.entrySet().iterator();
        int iArray=0;
        while (itr.hasNext())
        {
            Map.Entry entry = (Map.Entry) itr.next();
            ctrs[iArray]=new ConfigType();
            ctrs[iArray].setConfigKey(entry.getKey().toString() );
            ctrs[iArray].setConfigValue(entry.getValue() );
            iArray++;
        }

        return ctrs;
    }
    

}
