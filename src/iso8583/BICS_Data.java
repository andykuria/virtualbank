/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package iso8583;

import lib.CommonLib;

/**
 *
 * @author kt1
 */
public class BICS_Data {
    private BICS_Data_Type dataType;
    private byte[] data;

    public BICS_Data(BICS_Data_Type pdataType, byte[] pdata) {
        this.dataType = pdataType;
        this.data = pdata;
    }

    public BICS_Data(BICS_Data_Type pdataType) {
        this.dataType = pdataType;
        data=new byte[0];
    }
    
    public BICS_Data()
    {
        dataType=BICS_Data_Type.BANS;
        data=new byte[0];
    }

    public void setType(BICS_Data_Type pdataType)
    {
        this.dataType = pdataType;
    }

    public BICS_Data_Type getType()
    {
        return dataType;
    }

    public int getLength()
    {
        return (data==null)?0: data.length;
    }
    public void setData(byte[] pdata)
    {
        try
        {
            data=new byte[pdata.length];
            System.arraycopy(pdata, 0, data, 0, pdata.length);
        }
        catch (Exception ex)
        {
            data=new byte[0];
        }
    }

    public void setData(byte[] pdata,int pFromPos, int pLength)
    {
        try
        {
            
            data=new byte[pLength];
            System.arraycopy(pdata, pFromPos, data, 0, pLength);
        }
        catch (Exception ex)
        {
            data=new byte[0];
        }
    }

    public Object getValue()
    {
        try
        {
            switch (dataType)
            {
                case BANS:
                    return (Object)new String(data);

                case BBYTES:
                    return (Object)data;

                case BNUMBER:
                    return CommonLib.getIntFromByteArray(data);
                    
                case BANS_NUMBER:
                    return CommonLib.valueOf(new String(data));

            }
        }
        catch (Exception ex)
        {
            return null;
        }
        return null;    

    }





}
